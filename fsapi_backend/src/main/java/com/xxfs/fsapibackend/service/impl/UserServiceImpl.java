package com.xxfs.fsapibackend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxfs.fsapibackend.exception.BusinessException;
import com.xxfs.fsapibackend.mapper.InterfaceInfoMapper;
import com.xxfs.fsapibackend.mapper.UserMapper;
import com.xxfs.fsapibackend.model.vo.LoginVO;
import com.xxfs.fsapibackend.service.UserInterfaceInfoService;
import com.xxfs.fsapibackend.service.UserService;
import com.xxfs.fsapibackend.utils.JWTUtils;
import com.xxfs.fsapicommon.common.BaseResponse;
import com.xxfs.fsapicommon.common.ErrorCode;
import com.xxfs.fsapicommon.common.MyThreadPool;
import com.xxfs.fsapicommon.common.ResultUtils;
import com.xxfs.fsapicommon.model.dto.StudentDTO;
import com.xxfs.fsapicommon.model.entity.User;
import com.xxfs.fsapicommon.model.entity.UserInterfaceInfo;
import com.xxfs.fsapicommon.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.xxfs.fsapicommon.constant.UserConstant.ADMIN_ROLE;
import static com.xxfs.fsapicommon.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现类
 *
 * @author yupi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @DubboReference
    private StudentService studentService;

    private final MyThreadPool threadPool = MyThreadPool.getInstance();
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentNumber(userAccount);
        studentDTO.setPassword(userPassword);
        String json = JSONUtil.toJsonStr(studentDTO);
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8123" + "/api/crawler/checkStudent")
                .body(json)
                .execute();
        int status = httpResponse.getStatus();
        String result = httpResponse.body();
        log.error("status:{},result:{}", status, result);

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setStudentPassword(userPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 用户注册
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 新用户 id
     */
    @Override
    public BaseResponse<String> newUserRegister(String userAccount, String userPassword) throws IOException {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }

        // 2. 调用爬虫接口校验用户学号密码是否正确
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentNumber(userAccount);
        studentDTO.setPassword(userPassword);
        Boolean checkStudent = studentService.checkStudent(studentDTO);
        if (!checkStudent) {
            return ResultUtils.error(50000, "注册失败,用户密码错误");
        }
        User user = new User();
        // 3. 创建用户
        synchronized (userAccount.intern()) {
            // 1. 查询是否已经存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            Long count = baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                return ResultUtils.error(50000, "注册失败,用户存在");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据

            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setStudentPassword(userPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
        }
        Long id = user.getId();
        CompletableFuture.runAsync(() -> createUserInterfaceInfo(id), threadPool.getExecutorService()).exceptionally(ex -> {
            log.error("创建用户接口信息时出错", ex);
            return null; // or throw a custom exception
        });
        // 返回注册信息
        return ResultUtils.success("注册成功");
    }

    private void createUserInterfaceInfo(long userId) {

        List<Long> idList = interfaceInfoMapper.searchIdList();
        List<UserInterfaceInfo> userInterfaceInfoList = idList.stream().map(id -> {
            UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setInterfaceInfoId(id);
            userInterfaceInfo.setLeftNum(100);
            return userInterfaceInfo;
        }).collect(Collectors.toList());
        userInterfaceInfoService.saveBatch(userInterfaceInfoList);
    }

    /**
     * 修改用户accessKey
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 用户新accessKey
     */
    @Override
    public LoginVO changeUserAccessKey(String userAccount, String userPassword) {
        // TODO 验证用户密码是否正确
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        queryWrapper.eq(User::getUserPassword, encryptPassword);
        User user = baseMapper.selectOne(queryWrapper);
        // TODO 随机生成AccessKey并修改数据库
        String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
        user.setAccessKey(accessKey);
        // TODO 返回新AccessKey
        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(user, loginVO);
        return loginVO;
    }

    @Override
    public LoginVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        String token = JWTUtils.getToken(user);
        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(user, loginVO);
        loginVO.setToken(token);
        return loginVO;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

}




