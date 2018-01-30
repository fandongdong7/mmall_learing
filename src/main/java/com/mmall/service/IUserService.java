package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str, String type);

    ServiceResponse<String> forgetGetQuestion(String username);

    ServiceResponse checkQuestionAnswer(String username, String question, String answer);

    ServiceResponse forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServiceResponse resetPassword(String passwordOld, String passwordNew, User user);

    ServiceResponse updateUserInfo(User user);

    ServiceResponse getInformation(Integer userId);
}
