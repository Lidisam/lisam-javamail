package com.lisam.utils;

import com.lisam.pojo.ReceiveAllMailVo;
import com.lisam.pojo.ReceiveMailAttachVo;
import com.lisam.pojo.ReceiveMailVo;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

public class MailerUtil {

    // pop协议(收件)
    private final static String POP3 = "pop3";

    // smtp协议(发送)
    private final static String SMTP = "smtp";

    public static ReceiveAllMailVo receive(String email, String password, String host)
            throws Exception {
        return receive(email, password, host, false, "-1", -1, -1, false);
    }

    public static ReceiveAllMailVo receive(String email, String password, String host, boolean ssl)
            throws Exception {
        return receive(email, password, host, ssl, "-1", -1, -1, false);
    }

    public static ReceiveAllMailVo receive(String email, String password, String host, boolean ssl, int start, int end)
            throws Exception {
        return receive(email, password, host, ssl, "-1", start, end, false);
    }

    public static ReceiveAllMailVo receive(String email, String password, String host, boolean ssl, String port,
                                           int start, int end, boolean debug) throws Exception {
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
            messages = folder.getMessages(startIndex < 1 ? 1 : startIndex, folder.getMessageCount() - end);  //分页查询
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
    public static boolean send(String from, String password, String host, String to, String subject, String content,
                              boolean ssl, String port, String cc, String bcc, boolean debug, File[] attachs) throws Exception {
        if (host == null) throw new Exception("请设置smtpHost的值(xxx.setSmtpHost(\"smtp.xx.com\"))");
        //1、连接邮件服务器的参数配置
        Properties props = new Properties();
        // 设置ssl
        setSmtpSSL(ssl, props);
        //设置用户的认证方式
        props.setProperty("mail.smtp.auth", "true");
        //设置传输协议
        props.setProperty("mail.transport.protocol", SMTP);
        //设置发件人的SMTP服务器地址
        props.setProperty("mail.smtp.host", host);
        // 设置端口
        if (!"-1".equals(port)) props.setProperty("mail.smtp.socketFactory.port", port);
        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        if (debug) session.setDebug(true);
        //3、创建邮件的实例对象
        Message msg = getMimeMessage(session, from, to, subject, content, cc, bcc, attachs);
        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport();
        //设置发件人的账户名和密码
        transport.connect(from, password);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg, msg.getAllRecipients());

        //5、关闭邮件连接
        transport.close();
        return true;
    }

    /**
     * 获得创建一封邮件的实例对象
     *
     * @param session 会话信息
     * @throws MessagingException
     * @throws AddressException
     */
    private static MimeMessage getMimeMessage(Session session, String from, String receiver, String subject,
                                              String content, String cc, String bcc, File[] attachs) throws Exception {
        //1.创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //2.设置发件人地址
        msg.setFrom(new InternetAddress(from));
        // 3.设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
        // 设置接收者（可以有多个）
//        setSendTo(msg, receiver, MimeMessage.RecipientType.TO);
        msg.addRecipients(MimeMessage.RecipientType.TO, receiver);
        // 设置抄送者，可以有多个
        if (cc != null) msg.addRecipients(MimeMessage.RecipientType.CC, cc);
        // 设置密送者，可以有多个
        if (bcc != null) msg.addRecipients(MimeMessage.RecipientType.BCC, bcc);
        ;

        //4.设置邮件主题
        msg.setSubject(subject, "UTF-8");

        //5.下面是设置邮件正文
        if (attachs == null) {  //附件信息为空，发送的是普通文本信息
            msg.setContent(content, "text/html;charset=UTF-8");
        } else {
            // 6. 创建文本"节点"
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, "text/html;charset=UTF-8");

            // 7. 包装文本节点
            MimeMultipart mm_text = new MimeMultipart(text);

            // 8. 把 mm_text 封装成一个 BodyPart
            MimeBodyPart text_image = new MimeBodyPart();
            text_image.setContent(mm_text);

            // 9. 创建附件"节点"
            MimeBodyPart[] attachments = new MimeBodyPart[attachs.length];
            int len = attachs.length;
            for (int index = 0; index < len; index++) {
                MimeBodyPart attachment = new MimeBodyPart();
                // 读取本地文件
                DataHandler dh = new DataHandler(new FileDataSource(attachs[index]));
                // 将附件数据添加到"节点"
                attachment.setDataHandler(dh);
                // 设置附件的文件名（需要编码）
                attachment.setFileName(MimeUtility.encodeText(dh.getName()));
                attachments[index] = attachment;
            }

            // 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合"节点" / Multipart ）
            MimeMultipart mm = new MimeMultipart();
            mm.addBodyPart(text_image);
            for (MimeBodyPart attachment : attachments) {
                mm.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加
            }
            mm.setSubType("mixed");         // 混合关系

            // 11. 设置整个邮件的关系（将最终的混合"节点"作为邮件的内容添加到邮件对象）
            msg.setContent(mm);
            //设置邮件的发送时间,默认立即发送
            msg.setSentDate(new Date());
        }

        return msg;
    }

    /**
     * @param user    [邮箱]
     * @param pwd     [密码或token]
     * @param popHost [pop服务器地址]
     * @param ssl     [是否开启ssl]
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

    // 设置smtp ssl属性
    private static void setSmtpSSL(boolean ssl, Properties props) throws GeneralSecurityException {
        // 是否开启ssl
        if (ssl) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);
        }
    }

}
