package com.xxfs.fsapiinterface.controller;

import com.xxfs.fsapicommon.common.BaseResponse;
import com.xxfs.fsapicommon.common.ResultUtils;
import com.xxfs.fsapicommon.model.entity.SendMailModel;
import com.xxfs.fsapicommon.service.MailService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/mail")
public class MailController {
    @DubboReference
    private MailService mailService;

    @PostMapping("/send")
    public BaseResponse<String> sendMail(@RequestBody SendMailModel sendMailModel) throws MessagingException {
        Boolean isSuccess = mailService.sendMail(sendMailModel);
        if (isSuccess.equals(true)) {
            return ResultUtils.error(50000, "发送失败");
        }
        return ResultUtils.success("发送成功");
    }
}
