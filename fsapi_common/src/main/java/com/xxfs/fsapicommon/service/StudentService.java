package com.xxfs.fsapicommon.service;

import com.xxfs.fsapicommon.model.dto.StudentDTO;

import java.io.IOException;
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
}
