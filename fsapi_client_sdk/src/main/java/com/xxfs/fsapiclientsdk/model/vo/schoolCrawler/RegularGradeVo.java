package com.xxfs.fsapiclientsdk.model.vo.schoolCrawler;

import lombok.Data;

import java.util.List;

/**
 * 平时成绩
 */
@Data
public class RegularGradeVo {

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

    private List<RegularGradeDetailVo> regularGradeDetail;
}
