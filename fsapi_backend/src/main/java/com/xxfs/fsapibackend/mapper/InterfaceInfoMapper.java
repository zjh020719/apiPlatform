package com.xxfs.fsapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxfs.fsapibackend.model.vo.InterfaceInfoVO;
import com.xxfs.fsapicommon.model.entity.InterfaceInfo;

import java.util.List;

/**
 * @Entity com.xxfs.fsapibackend.model.entity.InterfaceInfo
 */
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {
    List<InterfaceInfoVO> listAllByUserIdInterfaceInfoVos(Long userId);

    List<Long> searchIdList();
}




