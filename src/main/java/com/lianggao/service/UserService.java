package com.lianggao.service;

import com.lianggao.bean.UserInfo;

import java.util.List;


public interface UserService {
    UserInfo login(UserInfo user);
    /////////////////////////////////
    /**
     *根据用户id获得用户名称
     */
    String getNameByEmployeeNum(String EmployeeNum);
}
