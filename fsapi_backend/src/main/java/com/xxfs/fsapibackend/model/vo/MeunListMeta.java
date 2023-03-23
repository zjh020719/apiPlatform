package com.xxfs.fsapibackend.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zjh
 */
@Data
@Accessors(chain = true)
public class MeunListMeta {

    /**
     * 图标
     */
    private String icon;

    /**
     * 标题
     */
    private String title;

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

}
