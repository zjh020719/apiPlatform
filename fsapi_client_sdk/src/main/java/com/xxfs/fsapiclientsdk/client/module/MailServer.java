package com.xxfs.fsapiclientsdk.client.module;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.xxfs.fsapiclientsdk.model.User;

import static com.xxfs.fsapiclientsdk.constant.HttpConstant.GATEWAY_HOST;
import static com.xxfs.fsapiclientsdk.utils.SignUtils.getHeaderMap;

/**
 * @author zjh
 */
public class MailServer {
    /**
     * 发送邮件
     *
     * @param user
     * @param accessKey
     * @param secretKey
     * @return
     */
    public boolean sendEmail(User user, String accessKey, String secretKey) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/interface/mail/sendMail")
                .addHeaders(getHeaderMap(json, accessKey, secretKey))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return true;
    }
}
