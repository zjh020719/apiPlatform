package com.xxfs.fsapiinterface.controller;

import com.xxfs.fsapicommon.common.BaseResponse;
import com.xxfs.fsapicommon.common.ResultUtils;
import com.xxfs.fsapicommon.model.dto.StudentDTO;
import com.xxfs.fsapicommon.service.StudentService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/crawler")
public class CrawlerController {
    @DubboReference
    private StudentService studentService;

    @PostMapping("/getCourse")
    public BaseResponse<Map<String, Object>> getCourse(@RequestBody StudentDTO studentDTO) throws IOException {
        Map<String, Object> data = null;
        try {
            data = studentService.loginAndGetCourse(studentDTO);
            return ResultUtils.success(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtils.error(50000, "账户或者密码错误");
    }

    @PostMapping("/checkStudent")
    public BaseResponse<String> checkStudent(@RequestBody StudentDTO studentDTO) throws IOException {

        Boolean checkStudent = studentService.checkStudent(studentDTO);
        if (Boolean.TRUE.equals(checkStudent)) {
            return ResultUtils.success("ok");
        }
        return ResultUtils.error(50000, "账户或者密码错误");
    }
}
