package eu.europa.esig.dss.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import eu.europa.esig.dss.web.model.UserAreaForm;

import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/user-area" })
public class UserAreaController {

    @RequestMapping(method = RequestMethod.GET)
    public final String showHome(Model model) {

        UserAreaForm u = new UserAreaForm();
        model.addAttribute("userAreaForm", u);

        System.out.println("GET");

        return "user-area";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public final Model updateNumber(Model model, @ModelAttribute("userAreaForm") @Valid UserAreaForm userAreaForm) {

        System.out.println(userAreaForm.getNumber());
        
        UserAreaForm u = new UserAreaForm();
        
        u.setNumber(userAreaForm.getNumber());

        System.out.println("POST");

        model.addAttribute("userAreaForm", u);

        return model;
    }
}
