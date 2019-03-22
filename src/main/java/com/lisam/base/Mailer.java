package com.lisam.base;

import com.lisam.pojo.ReceiveAllMailVo;
import com.lisam.pojo.ReceiveMailVo;
import com.lisam.utils.MailerUtil;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.security.GeneralSecurityException;

/**
 * 工具实力入口类
 * @author lisam 2019-03-23 19:43:36
 */
public class Mailer {

    // 邮箱
    private String email;

    // 邮箱密码或token
    private String password;

    // 是否ssl
    private boolean ssl = false;

    // 端口，默认可不填写
    private String port = "-1";

    // 分页的开始
    private int start = 50;

    // 分页的结束
    private int end = 0;

    // popHost
    private String popHost;

    // smtpHost
    private String smtpHost;

    public Mailer(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // 发送邮件
    public Object send(String to, String subject, String content) throws Exception {
        return MailerUtil.send(this.email, this.password, this.smtpHost, to, subject, content, this.ssl, this.port);
    }

    // 接收邮件
    public ReceiveAllMailVo receive() throws Exception {
        return MailerUtil.receive(this.email, this.password, this.popHost, this.ssl, this.start, this.end);
    }

    // 解析邮件
    public ReceiveMailVo parseMail(Message message) throws Exception {
        return MailerUtil.parseMail(message);
    }

    // 打开控制台打印
    public void openDebug() {
        MailerUtil.openDebug();
    }

    // 关闭控制台打印
    public void closeDebug() {
        MailerUtil.closeDebug();
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getPopHost() {
        return popHost;
    }

    public void setPopHost(String popHost) {
        this.popHost = popHost;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }
}
