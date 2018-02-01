package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
            TokenCache.setKey(TokenCache.TOKEN_PROFEX + username, token);
            return ServiceResponse.createBySuccess(token);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServiceResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServiceResponse.createByErrorMessage("参数错误,token需要传递");
        }
        //判断用户是否登录
        ServiceResponse<String> vali1Response = checkValid(username, Const.USERNAME);
        if (vali1Response.isSuccess()) {
            //用户不存在
            return ServiceResponse.createByErrorMessage("该用户未登录");
        }
//        判断本地缓存token是否有效
        if (StringUtils.isBlank(TokenCache.getValue(TokenCache.TOKEN_PROFEX + username))) {
            return ServiceResponse.createByErrorMessage("token已过期");
        }

        if (StringUtils.equals(forgetToken, TokenCache.getValue(TokenCache.TOKEN_PROFEX + username))) {
            int result = userMapper.updatePasswordByUsername(username, MD5Util.MD5EncodeUtf8(passwordNew));
            if (result > 0) {
                return ServiceResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServiceResponse.createByErrorMessage("token无效");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServiceResponse resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.
        // 因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int result = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (result == 0) {
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        //更新db
        int resutlDB = userMapper.updateByPrimaryKeySelective(user);
        if (resutlDB > 0) {
            return ServiceResponse.createBySuccessMessage("密码更新成功");
        }
        return ServiceResponse.createByErrorMessage("更新密码失败");
    }

    @Override
    public ServiceResponse updateUserInfo(User user) {
        //判断邮箱是否可用（除了自己的以外）
        int result = userMapper.checkUpdateEmail(user.getEmail(),user.getId());
        if (result > 0) {
            return ServiceResponse.createByErrorMessage("邮箱不可用");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setRole(user.getRole());
        updateUser.setCreateTime(user.getCreateTime());
        updateUser.setUpdateTime(new Date());
        updateUser.setAnswer(user.getAnswer());

        //更新db
        int resultDB = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultDB > 0) {
            return ServiceResponse.createBySuccess("更新用户信息成功", updateUser);
        }
        return ServiceResponse.createByErrorMessage("更新用户信息失败");
    }

    public ServiceResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }

    public ServiceResponse chekUserIsAdmin(User user) {
        if (user!=null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }
}
