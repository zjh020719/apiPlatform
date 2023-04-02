package com.xxfs.fsapicommon.service;

import com.xxfs.fsapicommon.model.dto.StudentDTO;
import com.xxfs.fsapicommon.model.vo.crawler.RegularGradeDetailVo;
import com.xxfs.fsapicommon.model.vo.crawler.RegularGradeVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface StudentService {
    /**
     * @param student
     * @return
     * @throws IOException
     */
    Map<String, Object> loginAndGetCourse(StudentDTO student) throws IOException;

    /**
     * @param student
     * @return
     * @throws IOException
     */
    Boolean checkStudent(StudentDTO student) throws IOException;

    /**
     * @param student
     * @return
     * @throws IOException
     */
    void login(StudentDTO student) throws IOException;

    /**
     * @return
     * @throws IOException
     */
    Map<String, Object> getCourse() throws IOException;

    /**
     * @return
     * @throws IOException
     */
    Map<String, Object> getCourse(String year, String semester) throws IOException;

    Map<String, Object> getStudentInfo(StudentDTO student) throws IOException;

    /**
     * 获取考勤信息
     *
     * @return
     */
    Map<String, Object> getAttendance(StudentDTO studentDTO) throws IOException;

    /**
     * 获取平时成绩
     *
     * @param student
     * @return
     */
    List<RegularGradeVo> getRegularGrade(StudentDTO student) throws IOException;

    /**
     * 获取平时成绩的细节
     *
     * @param href
     * @return
     * @throws IOException
     */
    List<RegularGradeDetailVo> getRegularGradeDetail(String href) throws IOException;
}
