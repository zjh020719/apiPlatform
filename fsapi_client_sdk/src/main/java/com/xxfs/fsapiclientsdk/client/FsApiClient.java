package com.xxfs.fsapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xxfs.fsapiclientsdk.client.module.MailServer;
import com.xxfs.fsapiclientsdk.client.module.SchoolCrawler;
import com.xxfs.fsapiclientsdk.model.User;
import com.xxfs.fsapiclientsdk.model.dto.StudentDTO;
import com.xxfs.fsapiclientsdk.model.vo.schoolCrawler.RegularGradeVo;
import com.xxfs.fsapicommon.common.BaseResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxfs.fsapiclientsdk.constant.HttpConstant.GATEWAY_HOST;
import static com.xxfs.fsapiclientsdk.utils.SignUtils.genSign;


/**
 * 调用第三方接口的客户端
 *
 * @author zjh
 */
public class FsApiClient {

    private final String accessKey;

    private final String secretKey;

    private MailServer mailServer;

    private SchoolCrawler schoolCrawler;

    public FsApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.mailServer = new MailServer();
        this.schoolCrawler = new SchoolCrawler();
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }

    /**
     * 邮件发送
     */
    public boolean sendEmail(User user) {
        return mailServer.sendEmail(user, accessKey, secretKey);
    }

    /**
     * 获取课表
     */
    public BaseResponse<Map> getCourse(StudentDTO studentDTO) {
        return schoolCrawler.getCourse(studentDTO, accessKey, secretKey);
    }

    /**
     * 获取个人信息
     */
    public BaseResponse<Map> getStudentInfo(StudentDTO studentDTO) {
        return schoolCrawler.getStudentInfo(studentDTO, accessKey, secretKey);
    }

    /**
     * 获取考勤信息
     */
    public BaseResponse<Map> getAttendance(StudentDTO studentDTO) {
        return schoolCrawler.getAttendance(studentDTO, accessKey, secretKey);
    }

    /**
     * 获取平时成绩
     */
    public BaseResponse<List<RegularGradeVo>> getRegularGrade(StudentDTO studentDTO) {
        return schoolCrawler.getRegularGrade(studentDTO, accessKey, secretKey);
    }

}
