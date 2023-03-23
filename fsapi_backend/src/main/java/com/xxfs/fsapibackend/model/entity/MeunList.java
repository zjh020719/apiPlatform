package com.xxfs.fsapibackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xxfs.fsapibackend.model.vo.MeunListMeta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zjh
 * @TableName admin_menu
 */
@TableName(value = "menu_list")
@Data
public class MeunList implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 父组件id
     */
    private Integer parentId;

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
    private String component;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 图标
     */
    private String icon;

    /**
     * 标题
     */
    private String title;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 外部连接地址
     */
    private String isLink;

    /**
     * 是否显示
     */
    private Boolean isHide;

    /**
     * 是否全屏
     */
    private Boolean isFull;

    /**
     * 是否固定
     */
    private Boolean isAffix;

    /**
     * 是否缓存
     */
    private Boolean isKeepAlive;

    @TableField(exist = false)
    private List<MeunList> children;

    @TableField(exist = false)
    private MeunListMeta meta;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}