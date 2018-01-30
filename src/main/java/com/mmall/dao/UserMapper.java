package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    String forgetGetQuestion(String username);

    int checkQuestionAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int updatePasswordByUsername(@Param("username") String username, @Param("password") String passwrod);

    int checkPassword(@Param("userId") Integer userId, @Param("password") String passwrod);

    int checkUpdateEmail(@Param("userId") Integer userId, @Param("email") String email);
}