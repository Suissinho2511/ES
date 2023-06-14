package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.web.dao.UserRepository;
import eu.europa.esig.dss.web.model.User;
import eu.europa.esig.dss.web.model.UserAreaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/user-area" })
public class UserAreaController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public final String showHome(Model model) {
        UserAreaForm u = new UserAreaForm();
        model.addAttribute("userAreaForm", u);
        System.out.println("GET");
        return "user-area";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public final Model updateNumber(Model model, @ModelAttribute("userAreaForm") @Valid UserAreaForm userAreaForm, HttpSession session) {
        String username = (String) session.getAttribute("username"); // Recupera o nome do usuário da sessão
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setNumber(userAreaForm.getNumber());
            userRepository.save(user);
            System.out.println("Number updated successfully");
        } else {
            System.out.println("User not found");
        }
        UserAreaForm u = new UserAreaForm();
        u.setNumber(userAreaForm.getNumber());
        System.out.println("POST");
        model.addAttribute("userAreaForm", u);
        return model;
    }
}
