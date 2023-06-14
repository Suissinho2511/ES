package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.web.dao.UserRepository;
import eu.europa.esig.dss.web.model.LoginForm;
import eu.europa.esig.dss.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/login" })
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public final String showLogin(Model model) {
        LoginForm l = new LoginForm();
        model.addAttribute("loginForm", l);
        System.out.println("GET");
        return "login";
    }

@RequestMapping(method = RequestMethod.POST)
@ResponseBody
public final String login(Model model, @ModelAttribute("loginForm") @Valid LoginForm login, HttpSession session){
    String username = login.getUsername();
    String password = login.getPassword();
    User user = userRepository.findByUsername(username);
    if (user != null && password.equals(user.getPassword())) {
        session.setAttribute("username", username); // Guarda o nome do usuário na sessão
        LoginForm l = new LoginForm();
        l.setPassword(login.getUsername());
        l.setUsername(login.getUsername());
        System.out.println("POST");
        model.addAttribute("loginForm", l);
        return "redirect:/user-area"; // Redireciona para a área do usuário após o login bem-sucedido
    } else {
        return "login"; // Retorna para a página de login em caso de falha no login
    }
}
