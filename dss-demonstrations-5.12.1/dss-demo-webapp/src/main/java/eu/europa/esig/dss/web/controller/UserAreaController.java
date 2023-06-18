package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.database.UserDataAccess;
import eu.europa.esig.dss.web.model.UserAreaForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/user-area" })
public class UserAreaController {

    @RequestMapping(method = RequestMethod.GET)
    public final String showHome(final Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser"); // Get username from session
        if (username == null) {
            session.setAttribute("message", "Please login first."); // Add message
            return "redirect:/login"; // Redirect to login page if not logged in
        }
        String phoneNumber = UserDataAccess.retrievePhoneNumber(username);
        model.addAttribute("number", phoneNumber);
        return "user-area";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public final Model updateNumber(Model model, @ModelAttribute("userAreaForm") @Valid UserAreaForm userAreaForm, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser"); // Get username from session
        if (username == null) {
            session.setAttribute("message", "Please login first."); // Add message
            return model; // Return the current model without making any changes if not logged in
        }
        UserDataAccess.modifyPhoneNumber(username, userAreaForm.getNumber());
        model.addAttribute("userAreaForm", userAreaForm);
        return model;
    }

}
