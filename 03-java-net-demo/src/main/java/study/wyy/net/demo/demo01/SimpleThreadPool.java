package study.wyy.net.demo.demo01;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ：wyaoyao
 * @date ： 2020-04-05 10:51
 * 增加 min max，active 自动扩容，自动回收
 */
@Slf4j
public class SimpleThreadPool extends Thread {

    // 线程的数量
    private int size;

    // 任务队列的大小
    private final int queueSize;


    // 默认任务队列的大小
    private final static int DEFAULT_QUEUE_SIZE = 2000;

    private static volatile int seq = 0;

    // 线程池里线程名字的前缀
    private final static String THREAD_NAME_PREFIX = "SIMPLE_THREAD_POOL-";

    // 线程组
    private final static ThreadGroup THREAD_GROUP = new ThreadGroup("Pool_Group");

    // 任务队列
    private final static LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();

    // 用于管理线程
    private final static List<ExecuteTask> THREAD_QUEUE = new ArrayList<>();

    // 拒绝策略
    private final DisCardPolicy disCardPolicy;

    // 线程池是否销毁
    private volatile boolean destroy = false;

    private int min;
    private int active;
    private int max;

    public static final DisCardPolicy DEFAULT_DISCARD_POLICY = () -> {
        throw new DisCardException("Task is disCard");
    };

    public SimpleThreadPool() {
        this(4, 8, 12, DEFAULT_QUEUE_SIZE, DEFAULT_DISCARD_POLICY);
    }

    public SimpleThreadPool(int min, int active, int max, int queueSize, DisCardPolicy disCardPolicy) {
        this.min = min;
        this.active = active;
        this.max = max;
        this.queueSize = queueSize;
        this.disCardPolicy = disCardPolicy;
        init();
    }

    /**
     * 提交任务
     * @param runnable
     */
    public void submit(Runnable runnable) {
        if (destroy) {
            throw new IllegalStateException("thread pool has destroy");
        }
        synchronized (TASK_QUEUE) {
            if (TASK_QUEUE.size() > queueSize) {
                disCardPolicy.discard();
            }
            TASK_QUEUE.addLast(runnable);
            // 通知那些wait的线程，有任务了
            TASK_QUEUE.notifyAll();
        }
    }

    private void init() {
        for (int i = 0; i < min; i++) {
            createExecuteTask();
        }
        this.size = min;
        // 启动自己，用于自动扩容，自动回收
        this.start();
    }

    private void createExecuteTask() {
        ExecuteTask executeTask = new ExecuteTask(THREAD_GROUP, THREAD_NAME_PREFIX + (seq++));
        // 启动
        executeTask.start();
        THREAD_QUEUE.add(executeTask);
    }


    // shutdown
    public void shutdown() throws InterruptedException {
        while (!TASK_QUEUE.isEmpty()) {
            // 如果任务队列不是空，说明还有任务未执行结束不能关闭
            // 就休眠一下，不要一直在这判断
            Thread.sleep(50);
        }
        // 任务队列已经空了，所有的任务已经开始执行，但不代表任务完成
        // 因为是异步的，可能还有任务还在running
        // 根据线程池管理的线程数来进行操作
        int threadSize = THREAD_QUEUE.size();
        while (threadSize > 0) {
            // 遍历线程池里创建的线程
            for (ExecuteTask task : THREAD_QUEUE) {
                if (task.taskStatue == TaskStatue.BLOCKED) {
                    // 如果是BLOCKED状态 ==》 就是在wait的时候（任务队列是空的时候，任务已经全部开始执行）
                    // 打断线程, 根据下面的run方法，在打断的时候会捕获到InterruptedException， 跳出到Outer的位置
                    // 跳出到Outer的位置: 这个线程的的run方法也就结束了
                    task.interrupt();
                    // 关闭这个线程（在打断的时候，有可能线程不是在wait，所以就不无法捕获InterruptedException，
                    // 这个线程下次唤醒的时候，又会进入while，所以这里将Thread的状态改为Dead）
                    task.close();
                    threadSize--;
                } else {
                    // 否则，就休息一下，不要疯狂运行
                    Thread.sleep(50);
                }
            }
        }
        this.destroy = true;
        log.info("ThreadPool has disposed");
    }

    /**
     * 线程池也是一个线程，重写run方法用于自动扩容
     */
    @Override
    public void run() {
        while (!destroy) {
            log.info("SimpleThreadPool => MIN:[{}],ACTIVE:[{}],MAX:[{}],currentThreadSize:[{}],QueueSize:[{}],",
                    this.min, this.active, this.max, this.size, TASK_QUEUE.size());
            try {
                Thread.sleep(5_000);
                // 扩容逻辑：当任务队列中任务数量已经超过了线程池中活跃的线程数量，
                // 但是此时线程池中的线程数量还是小于规定的活跃数量，那么就扩增到规定的活跃数量（active）
                if (TASK_QUEUE.size() > active && size < active) {
                    for (int i = size; i < active; i++) {
                        createExecuteTask();
                    }
                    log.info("The pool has incremented");
                    // 此时已经扩增到设定的活跃数量
                    size = active;
                } else if (TASK_QUEUE.size() > max && size < max) {
                    // 当天任务的数量已经大于规定线程池中最大线程数量，并且此时线程池中的线程数量是小于规定的线程池中最大数量
                    // 就扩增到最大数量
                    for (int i = size; i < max; i++) {
                        createExecuteTask();
                    }
                    log.info("The pool has incremented");
                    size = max;
                }

                // 释放线程数，释放到active
                // 当任务队列中已经空了，并且线程池中的线程数量已经超过了规定的活跃数量
                // 那么就要释放线程池中的线程数量，释放到活跃数量即可
                if (TASK_QUEUE.isEmpty() && size > active) {
                    log.info("Pool reduce => MIN:[{}],ACTIVE:[{}],MAX:[{}],currentThreadSize:[{}],QueneSize:[{}],",
                            this.min, this.active, this.max, this.size, TASK_QUEUE.size());
                    synchronized (TASK_QUEUE) {
                        // 计算要释放的数量
                        int releaseSize = size - active;
                        for (Iterator<ExecuteTask> it = THREAD_QUEUE.iterator(); it.hasNext(); ) {
                            if (releaseSize == 0){
                                break;
                            }
                            ExecuteTask next = it.next();
                            next.close();
                            next.interrupt();
                            it.remove();
                            releaseSize--;
                        }
                        size = active;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 是否销毁
    public boolean isDestroy() {
        return this.destroy;
    }

    // 封装Thread
    private static class ExecuteTask extends Thread {
        // 创建任务的时候默认是Free状态
        private volatile TaskStatue taskStatue = TaskStatue.Free;

        public ExecuteTask(ThreadGroup group, String name) {
            super(group, name);
        }


        // 重写run方法，不能让线程执行结束后，被jvm回收
        @Override
        public void run() {
            OUTER:
            while (this.taskStatue != TaskStatue.DEAD) {
                Runnable runnable;
                // 这个线程没有死掉，就去任务队列里取任务执行
                synchronized (TASK_QUEUE) {
                    while (TASK_QUEUE.isEmpty()) {
                        // 空的，说明没有提交任务，或者任务已经全部开始执行了，那么就只能等待（放到了TASK_QUEUE的wait队列中）
                        try {
                            taskStatue = TaskStatue.BLOCKED;
                            TASK_QUEUE.wait();
                        } catch (InterruptedException e) {
                            // wait的时候，如果别人打断线程，要跳出到OUTER位置
                            break OUTER;
                        }
                    }
                    // 唤醒了，抢到了锁，任务队列不是空，就要执行任务，
                    runnable = TASK_QUEUE.removeFirst();
                }
                // 一定要主要同步的范围，获取任务是需要同步的，但是任务的执行，就不能加锁了，否则就没有意义了
                // 否则会一个个的同步执行。！！！！！
                if (runnable != null) {
                    taskStatue = TaskStatue.RUNNING;
                    runnable.run();
                    taskStatue = TaskStatue.Free;
                }
            }
        }

        // 返回任务的状态
        public TaskStatue getTaskStatue() {
            return this.taskStatue;
        }

        // 关闭任务
        public void close() {
            this.taskStatue = TaskStatue.DEAD;
        }

    }


    // 枚举：线程的状态
    private enum TaskStatue {
        Free,
        RUNNING,
        BLOCKED,
        DEAD
    }

    // 拒绝策略，定义成public，可以由使用者自定义满足使用需求的策略
    @FunctionalInterface
    public interface DisCardPolicy {
        void discard() throws DisCardException;
    }

    // 定义一个异常
    public static class DisCardException extends RuntimeException {
        public DisCardException(String message) {
            super(message);
        }
    }

}

