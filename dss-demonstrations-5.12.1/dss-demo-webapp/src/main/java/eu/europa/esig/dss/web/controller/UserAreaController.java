package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.database.UserDataAccess;
import eu.europa.esig.dss.web.model.UserAreaForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/user-area" })
public class UserAreaController {

    @RequestMapping(method = RequestMethod.GET)
    public final String showHome(final Model model) {
        // TODO: CHANGE THIS TO THE USERNAME OF THE LOGGED IN USER
        String phoneNumber = UserDataAccess.retrievePhoneNumber("Vasco");
        System.out.println(phoneNumber);
        model.addAttribute("number", phoneNumber);
        return "user-area";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public final Model updateNumber(Model model,
            @ModelAttribute("userAreaForm") @Valid UserAreaForm userAreaForm) {
        String number = userAreaForm.getNumber();
        System.out.println(number);
        // TODO: CHANGE THIS TO THE USERNAME OF THE LOGGED IN USER
        UserDataAccess.modifyPhoneNumber("Vasco", number);
        System.out.println("changed");
        String phoneNumber = UserDataAccess.retrievePhoneNumber("Vasco");

        model.addAttribute("userAreaForm", userAreaForm);
        return model;
    }
}