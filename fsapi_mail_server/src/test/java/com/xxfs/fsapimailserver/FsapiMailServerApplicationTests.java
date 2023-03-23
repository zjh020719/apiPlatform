package com.xxfs.fsapimailserver;


import com.xxfs.fsapicommon.model.entity.SendMailModel;
import com.xxfs.fsapimailserver.service.impl.MailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.mail.MessagingException;

@SpringBootTest
class FsapiMailServerApplicationTests {

    @Resource
    private MailServiceImpl mailService;

    @Test
    void contextLoads() throws MessagingException {
        SendMailModel sendMailModel = new SendMailModel();
        String[] myArray = {"1246418154@qq.com"};
        sendMailModel.setSubject("肖锦阳").setSendContent("大傻逼").setRecipientMailbox(myArray);
        mailService.sendMail(sendMailModel);
    }

}
