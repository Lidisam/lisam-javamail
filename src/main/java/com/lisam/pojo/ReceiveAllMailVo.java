package com.lisam.pojo;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.util.Date;
import java.util.List;

/**
 * 邮箱收取的所有信息的实体类
 */
public class ReceiveAllMailVo {

    private Message[] messages;                    // 所有邮件消息
    private Folder folder;                         // 句柄，使用完需close
    private Store store;                           // 句柄，使用完需close
    private boolean status;                        // 接收状态
    private String err;                            // 错误信息

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
