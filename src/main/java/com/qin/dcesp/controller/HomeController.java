package com.qin.dcesp.controller;

import com.qin.dcesp.entity.User;
import com.qin.dcesp.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/index")
    public String getIndexPage(Model model) {
        User user = hostHolder.getUser();
        if(user == null){
            model.addAttribute("username",null);
        }else {
            model.addAttribute("username",user.getUsername());
        }
        return "/index";
    }

    @RequestMapping(path = "/workingSpace")
    public String getWorkingPage(){
        return "/site/working";
    }
}
