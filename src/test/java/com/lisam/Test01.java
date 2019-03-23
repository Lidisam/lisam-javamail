package com.lisam;

import com.lisam.base.Mailer;
import com.lisam.pojo.ReceiveAllMailVo;
import org.junit.Test;

import javax.mail.Message;
import java.io.File;

public class Test01 {


    @Test
    public void test01() throws Exception {
        Mailer mailer = new Mailer("xxxxx@163.com", "xxx");
        mailer.setSmtpHost("smtp.163.com")
                .setCc("xxxx@126.com")  //设置抄送人
//                .setBcc("xxxxx@126.com,xxxx@qq.com")  //设置密送人
//                .setSsl(true)  //设置ssl
//                .setDebug(true)   //开启控制台邮件信息打印
                .setAttachs(new File[]{
                        new File("C:\\Users\\lisam\\Desktop\\笔记图片\\线程池存取过程001.png"),
                        new File("C:\\Users\\lisam\\Desktop\\笔记图片\\前端后台原理交互图.png")
                })
                .send("xxxxxx@qq.com,xxxxxxx@126.com", "测试标题", "测试内容");
        ReceiveAllMailVo receiveAllMailVo = null;
        try {
            receiveAllMailVo = mailer
                    .setPopHost("pop.163.com")
//                    .setSsl(true)  //设置ssl
//                    .setDebugReceive(true)  //开启控制台邮件信息打印
//                    .setStart(100)  //设置拉取邮件的位置，start=100，end=50表示跳过最新邮件的50封，然后向前取50封，默认是取最新的50封(这时start=50, end=0)
//                    .setEnd(50)
                    .receive();
            Message[] messages = receiveAllMailVo.getMessages();
            for (Message message : messages) {
                // mailer.parseMail()可解析出完整的邮件信息
                System.out.println(mailer.parseMail(message).toString());
            }
        } finally {
            if (receiveAllMailVo != null) {
                receiveAllMailVo.getFolder().close(true);
                receiveAllMailVo.getStore().close();
            }
        }

    }

}
