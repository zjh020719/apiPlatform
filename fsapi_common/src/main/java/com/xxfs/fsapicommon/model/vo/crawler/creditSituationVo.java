package com.xxfs.fsapicommon.model.vo.crawler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 学分绩点情况返回类
 */
@Data
@AllArgsConstructor
public class creditSituationVo implements Serializable {

    /**
     * 累计已获得学分
     */
    private String doneScore;
    /**
     * 累计在读课程学分
     */
    private String studingScore;
    /**
     * 已选课程学分
     */
    private String doneCourseScore;
    /**
     * 预期获得学分
     */
    private String expectScore;
    /**
     * 平均学分绩点
     */
    private String gpa;
    /**
     * 本专业本年级毕业需修满学分
     */
    private String sumScore;
}
