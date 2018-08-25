package com.gzcc.controller;

import com.gzcc.entity.Admin;
import com.gzcc.entity.Message;
import com.gzcc.repository.AdminRepository;
import com.gzcc.utils.EncryptOrDecryptPassordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by jie on 2018/4/16.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;


    /**
     //     * 查询所有管理员的信息
     //     * @return
     */
    @GetMapping("/getlist")
    @ResponseBody
    public List<Admin> findAllStudent(){
        List<Admin> admins = adminRepository.findAll();
        return admins;
    }



    /**
     * 查看管理员的个人信息
     */
     @GetMapping("/findByAdminName")
     @ResponseBody
     public Admin findByAdminName(String adminName){
         Admin admin = adminRepository.findByAdminName(adminName);
         return admin;
     }


    /***
     * 修改管理员信息
     */
     @PostMapping("/edit")
     @ResponseBody
     public Message editAdmin(Admin admin){
          adminRepository.save(admin);
          Message message = new Message();
          message.setStatus(200);
          message.setErrorMsg("");
          return message;
     }




    /**
     *
     */
    @PostMapping("/addAdmin")
    @ResponseBody
    public Admin addAdmin(Admin admin){
        adminRepository.save(admin);
        return admin;
    }

    /***
     * 修改管理员密码
     */
    @GetMapping("/updateAdminPassword")
    @ResponseBody
    private boolean updateAdminPassword(@RequestParam("adminName")String adminName,@RequestParam("oldPassword")String oldPassword,
                                       @RequestParam("newPassword")String newPassword){
        boolean updateMessage = false;

        //判断用户名和密码是否一致
        Admin admin = adminRepository.findByAdminName(adminName);
        if(admin!=null){
                String decrptDbOldPassword = EncryptOrDecryptPassordUtils.decryptBasedDes(admin.getAdminPassword());
                if(admin.getAdminName().equals(adminName)&&decrptDbOldPassword.equals(oldPassword)){
                    admin.setAdminPassword(newPassword);
                    adminRepository.save(admin);
                    updateMessage = true;
                }else {
                    updateMessage = false;
                }
        }
        return updateMessage;
    }
}
