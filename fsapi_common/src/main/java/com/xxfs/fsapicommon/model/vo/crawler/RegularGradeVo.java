package com.xxfs.fsapicommon.model.vo.crawler;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 平时成绩
 */
@Data
public class RegularGradeVo implements Serializable {

    /**
     * 课程代码
     */
    private String courseCode;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 成绩细节
     */
//    private String href;

    /**
     * 成绩细节
     */
    private List<RegularGradeDetailVo> regularGradeDetail;
}
