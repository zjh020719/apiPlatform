package com.xxfs.fsapiclientsdk.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudentDTO implements Serializable {
    /**
     * 学号
     */
    private String studentNumber;
    /**
     * 密码
     */
    private String password;

    private String year;

    private String semester;
}
