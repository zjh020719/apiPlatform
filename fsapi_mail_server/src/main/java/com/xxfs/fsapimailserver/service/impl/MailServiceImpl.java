package com.xxfs.fsapimailserver.service.impl;


import com.xxfs.fsapicommon.model.entity.SendMailModel;
import com.xxfs.fsapicommon.service.MailService;
import com.xxfs.fsapimailserver.utils.SendMailUtil;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import javax.mail.MessagingException;


@DubboService
public class MailServiceImpl implements MailService {
    @Resource
    private SendMailUtil sendMailUtil;

    @Override
    public Boolean sendMail(SendMailModel model) throws MessagingException {
        return sendMailUtil.sendMail(model);
    }
}
