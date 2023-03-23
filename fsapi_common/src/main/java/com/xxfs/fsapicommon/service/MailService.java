package com.xxfs.fsapicommon.service;


import com.xxfs.fsapicommon.model.entity.SendMailModel;

import javax.mail.MessagingException;

public interface MailService {
    Boolean sendMail(SendMailModel model) throws MessagingException;

}
