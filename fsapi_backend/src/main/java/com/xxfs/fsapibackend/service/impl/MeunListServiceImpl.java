package com.xxfs.fsapibackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxfs.fsapibackend.mapper.MeunListMapper;
import com.xxfs.fsapibackend.model.entity.MeunList;
import com.xxfs.fsapibackend.model.vo.MeunListMeta;
import com.xxfs.fsapibackend.model.vo.MeunListQueryResponse;
import com.xxfs.fsapibackend.service.MeunListService;
import com.xxfs.fsapicommon.common.BaseResponse;
import com.xxfs.fsapicommon.common.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjh
 * @description 针对表【admin_menu】的数据库操作Service实现
 * @createDate 2022-11-23 21:42:30
 */
@Service
public class MeunListServiceImpl extends ServiceImpl<MeunListMapper, MeunList>
        implements MeunListService {

    @Override
    public BaseResponse<List<MeunListQueryResponse>> selectList(String userRole) {
        List<MeunList> adminMenus = null;
        if (userRole.equals("admin")) {
            adminMenus = baseMapper.selectList(null);
        } else if (userRole.equals("user")) {
            LambdaQueryWrapper<MeunList> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.notLike(MeunList::getPath, "/admin%");
            adminMenus = baseMapper.selectList(queryWrapper);
        }

        List<MeunList> menuList = adminMenus.stream().map(item -> {
            MeunListMeta meta = new MeunListMeta();
            meta.setIcon(item.getIcon()).setIsAffix(item.getIsAffix())
                    .setIsFull(item.getIsFull()).setIsHide(item.getIsHide())
                    .setIsKeepAlive(item.getIsKeepAlive()).setTitle(item.getTitle()).setIsLink(item.getIsLink());
            item.setMeta(meta);
            return item;
        }).collect(Collectors.toList());

        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("id");
        treeNodeConfig.setParentIdKey("parentId");
        treeNodeConfig.setChildrenKey("children");
        List<Tree<String>> trees = TreeUtil.build(menuList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setParentId(treeNode.getParentId().toString());
                    tree.putExtra("path", treeNode.getPath());
                    tree.setName(treeNode.getName());
                    tree.setWeight(treeNode.getSort());
                    if (StringUtils.isNotEmpty(treeNode.getRedirect())) {
                        tree.putExtra("redirect", treeNode.getRedirect());
                    }
                    if (StringUtils.isNotEmpty(treeNode.getComponent())) {
                        tree.putExtra("component", treeNode.getComponent());
                    }
                    tree.putExtra("meta", treeNode.getMeta());
                });
        List<MeunListQueryResponse> adminMenuQueryResponseList = trees.stream().map(item -> {
            MeunListQueryResponse adminMenuQueryResponse = new MeunListQueryResponse();
            BeanUtil.copyProperties(item, adminMenuQueryResponse);
            return adminMenuQueryResponse;
        }).collect(Collectors.toList());

        return ResultUtils.success(adminMenuQueryResponseList);
    }
}




