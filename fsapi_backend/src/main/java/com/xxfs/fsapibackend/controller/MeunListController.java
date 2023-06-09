package com.xxfs.fsapibackend.controller;

import com.google.gson.Gson;
import com.xxfs.fsapibackend.model.vo.MeunListQueryResponse;
import com.xxfs.fsapibackend.service.MeunListService;
import com.xxfs.fsapiclientsdk.client.FsApiClient;
import com.xxfs.fsapiclientsdk.model.dto.StudentDTO;
import com.xxfs.fsapiclientsdk.model.vo.schoolCrawler.RegularGradeVo;
import com.xxfs.fsapicommon.common.BaseResponse;
import com.xxfs.fsapicommon.model.entity.User;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zjh
 */
@Api(tags = "菜单模块")
@RestController
@RequestMapping("/menu")
@Slf4j
public class MeunListController {

  @Autowired
  private MeunListService adminMenuService;

  @Autowired
  private FsApiClient fsApiClient;

  /**
   * @return
   */
  @GetMapping("/list")
  public BaseResponse<List<MeunListQueryResponse>> menuList(HttpServletRequest request) {
    String body = request.getHeader("body");
    Gson gson = new Gson();
    User user = gson.fromJson(body, User.class);
    return adminMenuService.selectList(user.getUserRole());
  }

  @PostMapping("/list")
  public BaseResponse<List<RegularGradeVo>> menuList(@RequestBody StudentDTO studentDTO) {
    return fsApiClient.getRegularGrade(studentDTO);
  }
}
