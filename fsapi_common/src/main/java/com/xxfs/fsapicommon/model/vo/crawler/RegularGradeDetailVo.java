package com.xxfs.fsapicommon.model.vo.crawler;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegularGradeDetailVo implements Serializable {
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
