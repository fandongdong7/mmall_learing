package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServiceResponse.createByErrorMessage("username is not exist");
        }
        //todo password need md5
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("password is error");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("login success", user);
    }

    @Override
    public ServiceResponse<String> register(User user) {
        ServiceResponse<String> response = checkValid(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) {
            return response;
        }
        response = checkValid(user.getEmail(), Const.EMAIL);
        if (!response.isSuccess()) {
            return response;
        }
        //设置用户权限
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //将用户信息插入到db中
        int result = userMapper.insert(user);
        if (result == 0) {
            return ServiceResponse.createByErrorMessage("用户注册失败");
        }
        return ServiceResponse.createBySuccessMessage("用户注册成功");
    }

    @Override
    public ServiceResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //校验用户名和邮箱
            if (Const.USERNAME.equals(type)) {
                int result = userMapper.checkUsername(str);
                if (result > 0) {
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int result = userMapper.checkEmail(str);
                if (result > 0) {
                    return ServiceResponse.createByErrorMessage("用户邮箱已存在");
                }
            }
        } else {
            return ServiceResponse.createByErrorMessage("参数异常");
        }
        return ServiceResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServiceResponse<String> forgetGetQuestion(String username) {
        //判断用户是否存在
        ServiceResponse<String> vali1Response = checkValid(username, Const.USERNAME);
        if (vali1Response.isSuccess()) {
            //用户不存在
            return ServiceResponse.createByErrorMessage("该用户未登录");
        }
        String question = userMapper.forgetGetQuestion(username);
        if (StringUtils.isNotBlank(question)) {
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("用户问题不存在");
    }

    @Override
    public ServiceResponse checkQuestionAnswer(String username, String question, String answer) {
        int result = userMapper.checkQuestionAnswer(username, question, answer);
        if (result > 0) {
            String token = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,token);
            return ServiceResponse.createBySuccess(token);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

}
