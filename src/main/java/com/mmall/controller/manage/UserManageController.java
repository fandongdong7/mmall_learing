package com.mmall.controller.manage;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session) {

        ServiceResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            if (response.getData().getRole().equals(Const.Role.ROLE_ADMIN)) {
                //说明是管理员登录
                session.setAttribute(Const.CURRENT_USER, response.getData());
                return ServiceResponse.createBySuccessMessage("登录成功");
            } else {
                return ServiceResponse.createByErrorMessage("不是管理员无法登录");
            }
        }
        return response;
    }
}
