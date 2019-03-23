package com.lisam.base;

import com.lisam.pojo.ReceiveAllMailVo;
import com.lisam.pojo.ReceiveMailVo;
import com.lisam.utils.MailerUtil;
import sun.misc.ThreadGroupUtils;

import javax.mail.Message;
import java.io.File;

/**
 * 工具实力入口类
 *
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

    // cc 抄送者/密送者设置(可设置多个)
    private String cc;  //抄送者
    private String bcc; //密送者

    // 是否在控制台打印邮件信息
    private boolean debug = false; //密送者
    private boolean debugReceive = false; //密送者

    // 附件
    private File[] attachs = null;

    public Mailer(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // 发送邮件
    public boolean send(String to, String subject, String content) throws Exception {
        boolean res = MailerUtil.send(this.email, this.password, this.smtpHost, to, subject, content, this.ssl, this.port
                , this.cc, this.bcc, this.debug, this.attachs);
        this.cc = null;
        this.bcc = null;
        this.debug = false;
        this.attachs = null;
        return res;
    }

    // 接收邮件
    public ReceiveAllMailVo receive() throws Exception {
        ReceiveAllMailVo res = MailerUtil.receive(this.email, this.password, this.popHost, this.ssl, this.port, this.start, this.end, this.debugReceive);
        this.start = 50;
        this.end = 0;
        this.debugReceive = false;
        return res;
    }

    // 解析邮件
    public ReceiveMailVo parseMail(Message message) throws Exception {
        return MailerUtil.parseMail(message);
    }

    // 是否可连接上
    public boolean isConnected(String popHost, boolean ssl) {
        return MailerUtil.isConnected(this.email, this.password, popHost, ssl);
    }

    public String getEmail() {
        return email;
    }

    public Mailer setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Mailer setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isSsl() {
        return ssl;
    }

    public Mailer setSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public String getPort() {
        return port;
    }

    public Mailer setPort(String port) {
        this.port = port;
        return this;
    }

    public int getStart() {
        return start;
    }

    public Mailer setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public Mailer setEnd(int end) {
        this.end = end;
        return this;
    }

    public String getPopHost() {
        return popHost;
    }

    public Mailer setPopHost(String popHost) {
        this.popHost = popHost;
        return this;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public Mailer setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
        return this;
    }

    public String getCc() {
        return cc;
    }

    public Mailer setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public String getBcc() {
        return bcc;
    }

    public Mailer setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public Mailer setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isDebugReceive() {
        return debugReceive;
    }

    public Mailer setDebugReceive(boolean debugReceive) {
        this.debugReceive = debugReceive;
        return this;
    }

    public File[] getAttachs() {
        return attachs;
    }

    public Mailer setAttachs(File[] attachs) {
        this.attachs = attachs;
        return this;
    }
}
