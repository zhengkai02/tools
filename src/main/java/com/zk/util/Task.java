package com.lc.changeimage.demo.util;

import com.lc.changeimage.demo.service.MailService;
import com.lc.changeimage.demo.vo.MailVO;
import com.sun.mail.util.MailSSLSocketFactory;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Task extends QuartzJobBean {
  Logger logger = LoggerFactory.getLogger(Task.class);
  private static String HOST = "smtp.qq.com";// 指定发送邮件的主机smtp.qq.com(QQ)|smtp.163.com(网易)
  private static String AUTH_CODE = "utcksxbwurbbbbji";//发送邮箱的人  需要生成的授权码  参考地址 https://service.mail.qq.com/cgi-bin/help?subtype=1&&id=28&&no=1001256
  @Autowired MailService mailService;
  @Override
  protected void executeInternal(JobExecutionContext arg0) {
    logger.info("定时任务开始START");
    try {
      List<MailVO> list = mailService.selectMails();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date now = new Date();
      for (MailVO mailVO : list) {
        String sendDate = mailVO.getSendDate();
        Date date = sdf.parse(sendDate);
        if (now.after(date)) {
          logger.info("开始发送邮件id为:{}",mailVO.getId());
          sendMail(mailVO);
          mailService.updateMail(mailVO);
        }
      }
    } catch (Exception e) {
      logger.info("定时任务开始失败");
      e.printStackTrace();
    }
    logger.info("定时任务开始SUCCESS");
  }

  public static void sendMail(MailVO mailVO) throws Exception {
    Properties properties = System.getProperties(); // 获取系统属性
    properties.setProperty("mail.smtp.host", HOST); // 设置邮件服务器
    properties.setProperty("mail.smtp.auth", "true"); // 打开认证
    // QQ邮箱需要下面这段代码，163邮箱不需要
    MailSSLSocketFactory sf = new MailSSLSocketFactory();
    sf.setTrustAllHosts(true);
    properties.put("mail.smtp.ssl.enable", "true");
    properties.put("mail.smtp.ssl.socketFactory", sf);
    // 1.获取默认session对象
    Session session =
        Session.getDefaultInstance(
            properties,
            new Authenticator() {
              @Override
              public PasswordAuthentication getPasswordAuthentication() {

                // 发件人邮箱账号(这里不知道为什么 如果直接获取vo的数据 会发送失败，加上""之后就没问题)、授权码
                return new PasswordAuthentication( "" + mailVO.getFromMailer(), AUTH_CODE);
              }
            });
    // 2.创建邮件对象
    Message message = new MimeMessage(session);
    // 2.1设置发件人
    message.setFrom(new InternetAddress(mailVO.getFromMailer()));
    // 2.2设置接收人
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailVO.getToMailer()));
    // 2.3设置邮件主题
    message.setSubject(mailVO.getTitle());
    message.setContent(mailVO.getContent(), "text/html;charset=UTF-8");
    // 3.发送邮件
    Transport.send(message);
  }
}
