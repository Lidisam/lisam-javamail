package com.lisam.utils;

import com.lisam.pojo.ReceiveAllMailVo;
import com.lisam.pojo.ReceiveMailAttachVo;
import com.lisam.pojo.ReceiveMailVo;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class MailerUtil {

    // pop协议(收件)
    private final static String POP3 = "pop3";

    // smtp协议(发送)
    private final static String SMTP = "smtp";

    // 是否开启邮件收取调试信息打印，默认否
    private static boolean debug = false;

    public static ReceiveAllMailVo receive(String email, String password, String host)
            throws Exception {
        return receive(email, password, host, false, "-1", -1, -1);
    }

    public static ReceiveAllMailVo receive(String email, String password, String host, boolean ssl)
            throws Exception {
        return receive(email, password, host, ssl, "-1", -1, -1);
    }

    public static ReceiveAllMailVo receive(String email, String password, String host, boolean ssl,  int start, int end)
            throws Exception {
        return receive(email, password, host, ssl, "-1", start, end);
    }

    public static ReceiveAllMailVo receive(String email, String password, String host, boolean ssl, String port,
                                           int start, int end) throws Exception {
        if (host == null) throw new Exception("请设置popHost的值(xxx.setPopHost(\"pop.xx.com\"))");
        ReceiveAllMailVo result = new ReceiveAllMailVo();
        //1、连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置传输协议
        props.setProperty("mail.store.protocol", POP3);
        //设置收件人的POP3服务器
        props.setProperty("mail.pop3.host", host);
        // 是否开启ssl
        setPop3SSL(ssl, props);
        // 是否设置端口
        if (!"-1".equals(port)) {
            props.setProperty("mail.pop3.port", port);
        }
        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        if (debug) session.setDebug(true);

        Store store = session.getStore(POP3);
        //连接收件人POP3服务器
        store.connect(host, email, password);
        //获得用户的邮件账户，注意通过pop3协议获取某个邮件夹的名称只能为inbox
        Folder folder = store.getFolder("inbox");
        //设置对邮件账户的访问权限
        folder.open(Folder.READ_WRITE);

        //得到邮件账户的所有邮件信息
         int startIndex = folder.getMessageCount() - start + 1;  //end：folder.getMessageCount() - endNum
        Message[] messages = null;
        if (startIndex == -1 && end == -1) {
            messages = folder.getMessages();  //全部查询出来
        } else {
            messages = folder.getMessages(startIndex, folder.getMessageCount() - end);  //分页查询
        }

        result.setMessages(messages);
        result.setFolder(folder);
        result.setStore(store);
        result.setStatus(true);
        return result;
    }

    // 直接返回解析邮箱操作句柄
    public static MimeMessageParser getMimeMessageParser(Message message) throws Exception {
        return new MimeMessageParser((MimeMessage) message).parse();
    }

    // 解析邮箱，返回详细信息
    public static ReceiveMailVo parseMail(Message message) throws Exception {
        MimeMessageParser parser = new MimeMessageParser((MimeMessage) message).parse();

        // 获取数据
        ReceiveMailVo receiveMailVo = new ReceiveMailVo();
        receiveMailVo.setFrom(parser.getFrom());
        receiveMailVo.setCc(parser.getCc());
        receiveMailVo.setTo(parser.getTo());
        receiveMailVo.setReplyTo(parser.getReplyTo());
        receiveMailVo.setSubject(parser.getSubject());
        receiveMailVo.setHtmlContent(parser.getHtmlContent());
        receiveMailVo.setPlainContent(parser.getPlainContent());
        receiveMailVo.setSendDate(message.getSentDate());
        receiveMailVo.setAttachments(parser.getAttachmentList());
        return receiveMailVo;
    }

    // 解析获取附件名、附件的inputStream流
    public static ReceiveMailAttachVo getAttachMessage(DataSource dataSource) throws IOException {
        ReceiveMailAttachVo receiveMailAttachVo = new ReceiveMailAttachVo();
        BufferedInputStream ins = new BufferedInputStream(dataSource.getInputStream());
        try {
            receiveMailAttachVo.setFileName(dataSource.getName());  // 文件名
            receiveMailAttachVo.setFileInputStream(ins);            // 文件输入流
        } finally {
            ins.close();
        }
        return receiveMailAttachVo;
    }

    // 发送邮箱
    public static Object send(String from, String password, String host, String to, String subject, String content,
                              boolean ssl, String port) throws Exception {
        if (host == null) throw new Exception("请设置smtpHost的值(xxx.setSmtpHost(\"smtp.xx.com\"))");
        Properties prop = new Properties();
            prop.setProperty("mail.host", host);
            prop.setProperty("mail.transport.protocol", SMTP);
            prop.setProperty("mail.smtp.auth", "true");
            // 端口设置
            if (!"-1".equals(port)) {
                prop.setProperty("mail.smtp.port", port);
            }
            // SSL开启
            if (ssl) {  //用于处理hotmail问题 https://blog.csdn.net/April_Moon/article/details/83181860
                prop.put("mail.smtp.socketFactory.fallback","true");
                prop.put("mail.smtp.starttls.enable", "true");
            }
            //使用JavaMail发送邮件的5个步骤
            //1、创建session
            Session session = Session.getInstance(prop);
            //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            if (debug) session.setDebug(true);
            //2、通过session得到transport对象
            Transport ts = session.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            ts.connect(host, from, password);
            //4、创建邮件
            //创建邮件对象
            MimeMessage message = new MimeMessage(session);
            //指明邮件的发件人
            message.setFrom(new InternetAddress(from));
            //指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //邮件的标题
            message.setSubject(subject);
            //邮件的文本内容
            message.setContent(String.valueOf(content), "text/html;charset=UTF-8");
            //5、发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
            return true;
    }

    /**
     * @param user [邮箱]
     * @param pwd [密码或token]
     * @param popHost [pop服务器地址]
     * @param ssl [是否开启ssl]
     * @return 连接是否成功
     * 判断是否连接成功
     */
    public static boolean isConnected(String user, String pwd, String popHost, boolean ssl) {
        try {
            //1、连接邮件服务器的参数配置
            Properties props = new Properties();
            //设置传输协议
            props.setProperty("mail.store.protocol", "pop3");
            props.put("mail.pop3.connectiontimeout", 5000);  //5秒连接不上就超时
            //设置收件人的POP3服务器
            props.setProperty("mail.pop3.host", popHost);
            // 是否开启ssl
            setPop3SSL(ssl, props);
            //2、创建定义整个应用程序所需的环境信息的 Session 对象
            Session session = Session.getInstance(props);
            //设置调试信息在控制台打印出来
            Store store = session.getStore("pop3");
            //连接收件人POP3服务器
            store.connect(popHost, user, pwd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 设置pop3 ssl属性
    private static void setPop3SSL(boolean ssl, Properties props) throws GeneralSecurityException {
        // 是否开启ssl
        if (ssl) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.pop3.ssl.enable", "true");
            props.put("mail.pop3.ssl.socketFactory", sf);
        }
    }

    // 打开控制台打印
    public static void openDebug() {
        debug = true;
    }

    // 关闭控制台打印
    public static void closeDebug() {
        debug = false;
    }


}
