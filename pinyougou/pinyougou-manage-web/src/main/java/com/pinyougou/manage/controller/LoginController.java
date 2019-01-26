package com.pinyougou.manage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {

    /**
     * 从security认证信息中获取当前登录人信息v
     * @return 当前登录人
     */
    @GetMapping("/getUsername")
    public Map<String,Object> getUsernama(){
        Map<String,Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }
}
