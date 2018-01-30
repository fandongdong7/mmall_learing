package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession httpSession) {

        ServiceResponse<User> response = iUserService.login(username, password);
        //如果成功登陆将用户的信息放置到session中
        if (response.isSuccess()) {
            httpSession.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession httpSession) {
        //移除session即可
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccessMessage("登出成功");
    }

    /**
     * 用户注册
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 校验接口
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     */

    public ServiceResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage("当前用户未登录，无法获取用户信息");
        }
        return ServiceResponse.createBySuccess(user);
    }
    /**
     * 获取用户问题接口
     */
    @RequestMapping(value = "froget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username) {
        return iUserService.forgetGetQuestion(username);
    }

    /**
     * 验证用户问题是否正确
     */
    @RequestMapping(value = "froget_check_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse checkQuestionAnswer(String username, String question, String answer) {
        return iUserService.checkQuestionAnswer(username,question,answer);
    }
}
