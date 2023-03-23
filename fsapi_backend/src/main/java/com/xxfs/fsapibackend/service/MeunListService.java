package com.xxfs.fsapibackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xxfs.fsapibackend.model.entity.MeunList;
import com.xxfs.fsapibackend.model.vo.MeunListQueryResponse;
import com.xxfs.fsapicommon.common.BaseResponse;

import java.util.List;


/**
 * @author zjh
 * @description 针对表【admin_menu】的数据库操作Service
 * @createDate 2022-11-23 21:42:30
 */
public interface MeunListService extends IService<MeunList> {

    BaseResponse<List<MeunListQueryResponse>> selectList(String userRole);
}
