# lisam-javamail
以javamail为基础，封装好的邮件收发工具包

## 基本用法如下：
```java
public class Test {
    public static void main(String[] args) {
                Mailer mailer = new Mailer("xxxx@163.com", "密码。。。。。");
                // 1. 发邮件
                mailer.setSmtpHost("smtp.163.com")
                        .setCc("抄送人邮箱")
                        //                .setBcc("xxxxx@126.com,xxxx@qq.com")  //设置密送人
                        //                .setSsl(true)  //设置ssl
                        //                .setDebug(true)   //开启控制台邮件信息打印
                        .setAttachs(new File[]{
                                new File("C:\\Users\\lisam\\附件1地址"),
                                new File("C:\\Users\\lisam\\Desktop\\附件2地址")
                        })
                        .send("收件人1@qq.com,收件人2@126.com", "测试标题subject", "测试内容Content");
                mailer.setCc("xxxxxx@hotmail.com")
                        .setSsl(true)
                        .send("收件人1@163.com,收件人2@139.com", "测试标题subject2", "测试内容Content2");
                
                // 2. 收邮件
                ReceiveAllMailVo receiveAllMailVo = null;                
                try {
                    receiveAllMailVo = mailer
                            .setPopHost("pop.163.com")
                            //                    .setSsl(true)  //设置ssl
                            //                    .setDebugReceive(true)  //开启控制台邮件信息打印
                            //                    .setStart(100)  //设置拉取邮件的位置，start=100，end=50表示跳过最新邮件的50封，然后向前取50封，默认是取最新的50封(这时start=50, end=0)
                            //                    .setEnd(50)    // 将start=-1, end=-1就可以取得所有邮件信息
                            .receive();
                    Message[] messages = receiveAllMailVo.getMessages();
                    // 解析邮件内容并返回
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
```
> 备注：为了方便调用，有些属性如cc、bcc等调用后会初始化，但是如email、port、ssl不会初始化，其收发函数对应的初始化属性如下
````
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
````
