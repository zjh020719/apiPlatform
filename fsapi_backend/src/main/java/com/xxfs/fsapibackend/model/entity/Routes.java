package com.xxfs.fsapibackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName routes
 */
@TableName(value = "routes")
@Data
public class Routes implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String path;

    /**
     *
     */
    private String meta;

    /**
     *
     */
    private String component;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}