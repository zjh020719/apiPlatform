package com.xxfs.fsapiclientsdk.model.vo.schoolCrawler;

import lombok.Data;

@Data
public class RegularGradeDetailVo {
    /**
     * 平时成绩来源
     */
    private String source;
    /**
     * 平时成绩占比
     */
    private String ratio;
    /**
     * 最高分
     */
    private String maxGrade;
    /**
     * 实际成绩
     */
    private String realGrade;

}
