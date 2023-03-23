package com.xxfs.fsapibackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xxfs.fsapibackend.model.vo.LoginVO;
import com.xxfs.fsapicommon.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户注册
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 新用户 id
     */
    long newUserRegister(String userAccount, String userPassword);

    /**
     * 修改用户accessKey
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 用户新accessKey
     */
    long changeUserAccessKey(String userAccount, String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
}
