package com.lisam.pojo;

import java.io.BufferedInputStream;

/**
 * 接收邮箱附件信息的实体类
 */
public class ReceiveMailAttachVo {

    private String fileName;                                              // 文件名
    private BufferedInputStream fileInputStream;                          // 文件输入流

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BufferedInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(BufferedInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }
}
