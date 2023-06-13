package eu.europa.esig.dss.web.controller;

import eu.europa.esig.dss.web.model.LoginForm;
import eu.europa.esig.dss.web.model.User;  // importe a classe User
import eu.europa.esig.dss.web.service.UserService;  // importe a classe UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = { "/login" })
public class LoginController {

    private final UserService userService;  // adicione um campo para o UserService

    @Autowired  // injete o UserService no construtor
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public final Model login(Model model, @ModelAttribute("loginForm") @Valid LoginForm login){
        System.out.println(login.getPassword());
        System.out.println(login.getUsername());

        LoginForm l = new LoginForm();
        l.setPassword(login.getPassword());
        l.setUsername(login.getUsername());

        System.out.println("POST");

        model.addAttribute("loginForm", l);

        // verifique se o usuário já existe
        User existingUser = userService.findByUsername(login.getUsername());
        if (existingUser != null) {
            // o usuário já existe, verifique a senha
            if (!existingUser.getPassword().equals(login.getPassword())) {
                // a senha fornecida não corresponde à senha armazenada para esse usuário
                // você pode retornar uma mensagem de erro ou redirecionar para a página de login
            }
        } else {
            //TODO: criar registo de utilizador
            // crie um novo usuário e salve-o no banco de dados
            User user = new User();
            user.setUsername(login.getUsername());
            user.setPassword(login.getPassword());
            userService.save(user);
        }

        return model;
    }
}
