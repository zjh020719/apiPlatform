package com.xxfs.fsapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxfs.fsapicommon.model.entity.InterfaceInfo;


/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
