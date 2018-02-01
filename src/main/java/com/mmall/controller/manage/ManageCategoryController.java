package com.mmall.controller.manage;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.IManageCategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 后台分类管理
 */
@Controller
@RequestMapping("/manage/category")
public class ManageCategoryController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IManageCategoryService iManageCategoryService;

    /**
     * 添加分类
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServiceResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        //校验用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
//        检查用户是否是admin角色
        ServiceResponse serviceResponse = iUserService.chekUserIsAdmin(user);
        if (serviceResponse.isSuccess()) {
            //进行添加分类的业务逻辑处理
            return iManageCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员登录");
        }
    }

    /**
     * 修改分类名称
     */

    @RequestMapping("set_category.do")
    @ResponseBody
    public ServiceResponse setCategory(HttpSession session, String catetoryName, Integer categoryId) {
        //校验用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        //检查用户是否是admin角色
        ServiceResponse serviceResponse = iUserService.chekUserIsAdmin(user);
        if (serviceResponse.isSuccess()) {
            //进行添加分类的业务逻辑处理
            return iManageCategoryService.updateCategory(catetoryName, categoryId);
        } else {
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员登录");
        }
    }

    /**
     * 查询分类下的同级子节点
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServiceResponse<List<Category>> getChildrenCategory(HttpSession session,
                                                               @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {

        //校验用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        //检查用户是否是admin角色
        ServiceResponse serviceResponse = iUserService.chekUserIsAdmin(user);
        if (serviceResponse.isSuccess()) {
            //进行添加分类的业务逻辑处理
            return iManageCategoryService.getChildrenCategory(categoryId);
        } else {
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员登录");
        }
    }

    /**
     * 递归查询一个节点下的所有节点
     * @param session
     * @param categoryId
     * @return
     */
    public ServiceResponse<List<Integer>> getDeepChildren(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        //校验用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        //检查用户是否是admin角色
        ServiceResponse serviceResponse = iUserService.chekUserIsAdmin(user);
        if (serviceResponse.isSuccess()) {
            //进行添加分类的业务逻辑处理
            return iManageCategoryService.getDeepChildre(categoryId);
        } else {
            return ServiceResponse.createByErrorMessage("无操作权限，需要管理员登录");
        }
    }
}

