package com.codefellows.authdemo;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthenticationController
{
    @Autowired
    ChatUserRepository chatUserRepository;

    @GetMapping("/login")
    public String getLoginPage()
    {
        return "/login.html";
    }

    @GetMapping("/signup")
    public String getSignupPage()
    {
        return "/signup.html";
    }

    @PostMapping("/login")
    public RedirectView logInUser(Model m, String username, String password)
    {
        ChatUser userFromDb = chatUserRepository.findByUsername(username);
        // WARNING: Old, insecure way
        //if ((userFromDb == null)
        //    || (!userFromDb.password.equals(password)))
        if ((userFromDb == null)
            || (!BCrypt.checkpw(password, userFromDb.password)))
        {
            return new RedirectView("/login");
        }

        return new RedirectView("/");
    }

    @PostMapping("/signup")
    public RedirectView signUpUser(Model m, String username, String password)
    {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        ChatUser newChatUser = new ChatUser(username, hashedPassword);

        chatUserRepository.save(newChatUser);
        return new RedirectView("/login");
    }
}
