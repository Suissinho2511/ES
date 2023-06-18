package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.database.UserDataAccess;
import eu.europa.esig.dss.web.model.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/login" })
public class LoginController {

    @RequestMapping(method = RequestMethod.GET)
    public final String showLogin(Model model, HttpSession session) {
        LoginForm l = new LoginForm();
        model.addAttribute("loginForm", l);
        
        if (session.getAttribute("message") != null) {
            model.addAttribute("errorMessage", session.getAttribute("message"));
            session.removeAttribute("message"); // Remove the attribute
        }

        return "login";
    }

    @RequestMapping(method = RequestMethod.POST)
    public final String login(Model model, @ModelAttribute("loginForm") @Valid LoginForm login, HttpSession session, RedirectAttributes redirectAttributes){
        String username = login.getUsername();
        String password = login.getPassword(); // Store the plain text password

        // DB call
        boolean success = UserDataAccess.userLogin(username, password);
        if (success) {
            session.setAttribute("loggedInUser", username); // Store username in session
            redirectAttributes.addFlashAttribute("successMessage", "Login successful!");
            return "redirect:/home"; // redirect to home after successful login
        }
        else {
            LoginForm l = new LoginForm();
            model.addAttribute("loginForm", l);
            model.addAttribute("errorMessage", "Login failed. Please try again."); // Show error message on login page
            return "login";
        }
    }
}