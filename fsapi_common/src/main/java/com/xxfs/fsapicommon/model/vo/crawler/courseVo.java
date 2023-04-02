package com.xxfs.fsapicommon.model.vo.crawler;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程返回类
 */
@Data
public class courseVo implements Serializable {

    /**
     * 课程代码
     */
    private String courseCode;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 课程学分
     */
    private String credits;
    /**
     * 授课方式
     */
    private String method;
    /**
     * 成绩
     */
    private String score;
}
