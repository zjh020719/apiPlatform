package com.xxfs.fsapimailserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zjh
 */
@Component("DeployParameter")
public class DeployParameter {
    /**
     * 发送者邮箱
     */
    public static String MAIL_USERNAME;

    @Value("${spring.mail.username}")
    public void setMailUsername(String mailUsername) {
        MAIL_USERNAME = mailUsername;
    }
    /**
     * 文件上传地址
     */
//    public static String UPLOAD_URL;
//    @Value("${file.upload.url}")
//    public void setUploadUrl(String uploadUrl) {
//        UPLOAD_URL = uploadUrl;
//    }
}

