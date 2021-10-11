package com.codefellows.dinobook.controller;

import com.codefellows.dinobook.model.DinoUser;
import com.codefellows.dinobook.repositories.DinoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class DinoBookController
{
    @Autowired
    DinoUserRepository dinoUserRepository;

    @GetMapping("/")
    public String getHomePage(Principal p, Model m)
    {
        if (p != null)
        {
            String username = p.getName();
            DinoUser dinoUser = dinoUserRepository.findByUsername(username);

            m.addAttribute("username", username);
            m.addAttribute("nickname", dinoUser.getNickname());
        }

        return "index.html";
    }

    @GetMapping("/test")
    public String getTestPage(Principal p, Model m)
    {
        if (p != null)  // not strictly required if your WebSecurityConfig is correct
        {
            String username = p.getName();
            DinoUser dinoUser = dinoUserRepository.findByUsername(username);

            m.addAttribute("username", username);
            m.addAttribute("nickname", dinoUser.getNickname());
        }

        return "/test.html";
    }

    @GetMapping("/users/{id}")
    public String getUserInfo(Model m, Principal p, @PathVariable Long id)
    {
        if (p != null)  // not strictly required if your WebSecurityConfig is correct
        {
            String username = p.getName();
            DinoUser dinoBrowsingUser = dinoUserRepository.findByUsername(username);

            m.addAttribute("username", username);
            m.addAttribute("nickname", dinoBrowsingUser.getNickname());
        }

        DinoUser dinoUser = dinoUserRepository.findById(id).orElseThrow();
        m.addAttribute("dinoUserUsername", dinoUser.getUsername());
        m.addAttribute("dinoUserNickname", dinoUser.getNickname());
        m.addAttribute("dinoUserId", dinoUser.getId());

        return "/user-info.html";
    }
}
