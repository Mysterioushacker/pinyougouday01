package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbUser user,String smsCode) {
        try {
            if(PhoneFormatCheckUtils.isPhoneLegal(user.getPhone())){
                if(userService.checkSmsCode(user.getPhone(),smsCode)){
                    user.setStatus("Y");
                    user.setCreated(new Date());
                    user.setUpdated(user.getCreated());
                    user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                    userService.add(user);
                    return Result.ok("用户注册成功");
                }else {
                    return Result.fail("验证码输入错误，用户注册失败");
                }
            }else {
                    return Result.fail("手机号输入格式有误，用户注册失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("用户注册失败");
    }

    @GetMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param user 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbUser user, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.search(page, rows, user);
    }

    /**
     * 对手机号发送短信验证码
     * 手机号应该在前端和后台都校验是否正确
     * @param phone
     * @return
     */
    @GetMapping("/sendSmsCode")
    public Result sendSmsCode(String phone){
        try {
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)){
                userService.sendSmsCode(phone);
                return Result.ok("发送短信验证码成功");
            }else {
                return Result.fail("手机号码输入有误,发送短信验证码失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("发送短信验证码失败！");
    }


    /**
     * 获取当前登录用户信息
     * @return
     */
    @GetMapping("getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }
}
