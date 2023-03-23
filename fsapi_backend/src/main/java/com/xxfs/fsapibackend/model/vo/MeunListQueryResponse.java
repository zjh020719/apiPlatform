package com.xxfs.fsapibackend.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


/**
 * @author zjh
 */
@Data
public class MeunListQueryResponse {

    /**
     * 路由地址
     */
    private String path;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 组件地址
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String component;

    /**
     * 重定向地址
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String redirect;


    private MeunListMeta meta;

    // 下级列表
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<MeunListQueryResponse> children;
}
