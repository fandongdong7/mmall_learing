package com.mmall.service.impl;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        //todo password md5
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("password ");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("login success", user);
    }
}
