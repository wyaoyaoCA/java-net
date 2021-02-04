package study.wyy.net.socket.mail;

import lombok.Data;
import lombok.ToString;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/4 10:19
 */
@ToString
@Data
public class Mail {

    public Mail(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.data = "Subject: "+ subject + "\r\n" + content;
    }

    /****
     * 发送者的邮件地址
     */
    private String from;

    /***
     * 接收者的邮件地址
     */
    private String to;

    /***
     * 邮件主题
     */
    private String subject;


    /****
     * 邮件正文
     */
    private String content;
    /*****
     * 邮件内容：包括邮件主题和正文
     */
    private String data;
}
