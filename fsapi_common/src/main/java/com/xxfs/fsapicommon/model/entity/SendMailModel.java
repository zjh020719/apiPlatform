package com.xxfs.fsapicommon.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SendMailModel implements Serializable {

    /**
     * 邮件主题
     */
    private String subject;


    /**
     * 收件人邮箱
     */
    private String[] recipientMailbox;

    /**
     * 抄送人邮箱
     */
    private String[] ccMailbox;

    /**
     * 加密抄送人邮箱
     */
    private String[] bccMailbox;
    /**
     * 发送内容
     */
    private String sendContent;


    /**
     * 真实名称/附件路径
     */
//    private Map<String,String> enclosures;

//    @ApiModelProperty(value = "附件是否添加的到正文,默认false不添加")
//    private Boolean is_;


}
