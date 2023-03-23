package com.xxfs.fsapimailserver.utils;

import com.xxfs.fsapicommon.model.entity.SendMailModel;
import com.xxfs.fsapimailserver.config.DeployParameter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Component
public class SendMailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    public Boolean sendMail(SendMailModel model) throws MessagingException {
        // 创建 MimeMessage 对象
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 使用 MimeMessageHelper 对象构造 MimeMessage
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // 设置邮件标题
        helper.setSubject(model.getSubject());
        // 设置发送者邮箱
        helper.setFrom(DeployParameter.MAIL_USERNAME);
        // 设置收件人邮箱
        helper.setTo(model.getRecipientMailbox());

        if (model.getCcMailbox() != null && model.getCcMailbox().length != 0) {
            // 设置抄送人
            helper.setCc(model.getCcMailbox());
        }

        if (model.getBccMailbox() != null && model.getBccMailbox().length != 0) {
            // 设置加密抄送
            helper.setBcc(model.getBccMailbox());
        }

        // 设置发送日期
        helper.setSentDate(new Date());
        // 设置发送内容
        helper.setText(model.getSendContent());

        // 发送邮件
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}

