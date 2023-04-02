package com.xxfs.fsapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxfs.fsapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @Entity com.xxfs.fsapibackend.model.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

    Long searchLeftNum(long interfaceInfoId, long userId);
}




