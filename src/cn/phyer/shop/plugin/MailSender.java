package cn.phyer.shop.plugin;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender implements Runnable {
    private String addr;
    private String title;
    private String content;

    public MailSender(String addr, String title, String content) {
        this.addr = addr;
        this.title = title;
        this.content = content;
    }

    @Override
    public void run() {
        try {
            sendMail(addr, title, content);
        }catch (Exception ignore){}
    }
    // 发送邮件
    public static void sendMail(String addr, String title, String content) throws MessagingException {
        Properties para = new Properties();
        para.put("mail.transport.protocol","smtp");//协议
        para.put("mail.smtp.host","smtp.exmail.qq.com");
        para.put("mail.smtp.port",465);//端口号
        para.put("mail.smtp.auth","true");
        para.put("mail.smtp.ssl.enable","true");//设置ssl安全连接

        javax.mail.Session session = javax.mail.Session.getInstance(para);
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress("admin@phyer.cn"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(addr));
        message.setSubject(title);
        message.setContent(content, "text/html;charset=utf-8");

        Transport transport = session.getTransport();
        transport.connect("admin@phyer.cn", "Cq19990711");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
