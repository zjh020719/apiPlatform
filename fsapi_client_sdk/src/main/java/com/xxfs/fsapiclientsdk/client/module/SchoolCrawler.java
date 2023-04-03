package com.xxfs.fsapiclientsdk.client.module;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.xxfs.fsapiclientsdk.model.dto.StudentDTO;
import com.xxfs.fsapiclientsdk.model.vo.schoolCrawler.RegularGradeVo;
import com.xxfs.fsapicommon.common.BaseResponse;
import com.xxfs.fsapicommon.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.xxfs.fsapiclientsdk.constant.HttpConstant.GATEWAY_HOST;
import static com.xxfs.fsapiclientsdk.utils.SignUtils.getHeaderMap;

/**
 * @author zjh
 */
@Slf4j
public class SchoolCrawler {
  public BaseResponse<Map> getCourse(StudentDTO studentDTO, String accessKey, String secretKey) {

    String json = JSONUtil.toJsonStr(studentDTO);
    HttpResponse httpResponse =
            HttpRequest.post(GATEWAY_HOST + "/api/interface/crawler/getCourse")
                    .addHeaders(getHeaderMap(json, accessKey, secretKey))
                    .body(json)
                    .execute();
    String result = httpResponse.body();
    try {
      return JSONUtil.toBean(result, BaseResponse.class);
    } catch (Exception exception) {
      return ResultUtils.error(50000, result);
    }
  }

  public BaseResponse<Map> getStudentInfo(
          StudentDTO studentDTO, String accessKey, String secretKey) {
    String json = JSONUtil.toJsonStr(studentDTO);
    HttpResponse httpResponse =
            HttpRequest.post(GATEWAY_HOST + "/api/interface/crawler/getStudentInfo")
                    .addHeaders(getHeaderMap(json, accessKey, secretKey))
                    .body(json)
                    .execute();
    String result = httpResponse.body();
    try {
      return JSONUtil.toBean(result, BaseResponse.class);
    } catch (Exception exception) {
      return ResultUtils.error(50000, result);
    }
  }

  public BaseResponse<Map> getAttendance(
          StudentDTO studentDTO, String accessKey, String secretKey) {
    String json = JSONUtil.toJsonStr(studentDTO);
    HttpResponse httpResponse =
            HttpRequest.post(GATEWAY_HOST + "/api/interface/crawler/getAttendance")
                    .addHeaders(getHeaderMap(json, accessKey, secretKey))
                    .body(json)
                    .execute();
    String result = httpResponse.body();
    try {
      return JSONUtil.toBean(result, BaseResponse.class);
    } catch (Exception exception) {
      return ResultUtils.error(50000, result);
    }
  }

  public BaseResponse<List<RegularGradeVo>> getRegularGrade(
          StudentDTO studentDTO, String accessKey, String secretKey) {
    String json = JSONUtil.toJsonStr(studentDTO);
    HttpResponse httpResponse =
            HttpRequest.post(GATEWAY_HOST + "/api/interface/crawler/getRegularGrade")
                    .addHeaders(getHeaderMap(json, accessKey, secretKey))
                    .body(json)
                    .execute();
    String result = httpResponse.body();
    log.error("result:{}", result);
    try {
      return JSONUtil.toBean(result, BaseResponse.class);
    } catch (Exception exception) {
      return ResultUtils.error(50000, result);
    }
  }
}
