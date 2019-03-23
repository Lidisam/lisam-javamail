package com.lisam.pojo;

import javax.activation.DataSource;
import javax.mail.Address;
import java.util.Date;
import java.util.List;

/**
 * 接收邮箱信息的实体类
 */
public class ReceiveMailVo {

    private String from;                        // 获取发件人地址
    private List<Address> cc;                          // 获取抄送人地址
    private List<Address> to;                          // 获取收件人地址
    private String replyTo;                     // 获取回复邮件时的收件人
    private String subject;                     // 获取邮件主题
    private String htmlContent;                 // 获取Html内容
    private String plainContent;                // 获取纯文本邮件内容（注：有些邮件不支持html）
    private Date sendDate;                      // 发送日期
    private List<DataSource> attachments;       // 附件信息

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<Address> getCc() {
        return cc;
    }

    public void setCc(List<Address> cc) {
        this.cc = cc;
    }

    public List<Address> getTo() {
        return to;
    }

    public void setTo(List<Address> to) {
        this.to = to;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getPlainContent() {
        return plainContent;
    }

    public void setPlainContent(String plainContent) {
        this.plainContent = plainContent;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public List<DataSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DataSource> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "[from: " + this.from + "," +
                "[sendDate: " + this.sendDate + "," +
                "[attachments 附件数量: " + this.attachments.size() + "]";
    }
}
