package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.web.model.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/login" })
public class LoginController {

    @RequestMapping(method = RequestMethod.GET)
    public final String showLogin(Model model) {
        LoginForm l = new LoginForm();
        model.addAttribute("loginForm", l);
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public final Model login(Model model, @ModelAttribute("loginForm") @Valid LoginForm login){
        login.setPassword(login.getUsername());
        login.setUsername(login.getUsername());
        model.addAttribute("loginForm", login);
        return model;
    }
}