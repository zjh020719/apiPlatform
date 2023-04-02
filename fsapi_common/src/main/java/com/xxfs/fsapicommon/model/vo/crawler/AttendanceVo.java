package com.xxfs.fsapicommon.model.vo.crawler;

import lombok.Data;

import java.io.Serializable;

/**
 * 考勤情况
 */
@Data
public class AttendanceVo implements Serializable {
    /**
     * 课程代码
     */
    private String courseCode;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 考勤状态
     */
    private String attendanceStats;
}
