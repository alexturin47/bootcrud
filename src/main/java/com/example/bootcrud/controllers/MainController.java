package com.example.bootcrud.controllers;

import com.example.bootcrud.dto.UserDto;
import com.example.bootcrud.entities.User;
import com.example.bootcrud.services.RoleServce;
import com.example.bootcrud.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.security.Principal;

@Controller
public class MainController {

    private final UserService userService;
    private final RoleServce roleServce;

    @Autowired
    public MainController(UserService userService, RoleServce roleServce) {
        this.userService = userService;
        this.roleServce = roleServce;
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }


    // перенаправление в зависимости от роли
    @GetMapping("/authorized")
    public String pageForAuthenticatedUsers(Principal principal) {
        UserDto user = userService.findByUsername(principal.getName());
        if(user.hasAuthorities("ADMIN")) {
            return "redirect:admin";
        } else {
            return "redirect:user";
        }
    }


    // вызов страницы админа
    @GetMapping("/admin")
    public String adminPage(Model model, @ModelAttribute("user") User user) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleServce.index());
        return "admin";
    }


    // редактирование юзера
    @GetMapping("/edit/{username}")
    public String edit(Model model, @PathVariable("username") String username) {
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("roles", roleServce.index());
        return "/edit";
    }

    @PatchMapping( "/update/{username}")
    public String updateAdmin(@ModelAttribute("user") @Valid UserDto userDto
            ,BindingResult bindingResult
            , @PathVariable("username") String username
            ,@RequestParam("roles") String[] roles) throws ValidationException {

        if(bindingResult.hasErrors()) {
            return "/edit/{username}";
        }
        userDto.setId(userService.findByUsername(username).getId());
        userDto.setRoles(roleServce.getRoleSet(roles));
        userService.updateUser(userDto);
        return "redirect:/authorized";
    }



    // созадние нового юзера
    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("roles", roleServce.index());
        return "/new";
    }

    @PostMapping("/new")
    public String create(Model model, @ModelAttribute("user") @Valid UserDto userDto
            , BindingResult bindingResult
            , @RequestParam(name = "roles", required = false) @Valid String[] roles) throws ValidationException {

        if(bindingResult.hasErrors()) {
            model.addAttribute("roles", roleServce.index());
            return "/new";
        }

        userDto.setRoles(roleServce.getRoleSet(roles));
        userService.saveUser(userDto);
        return "redirect:/admin";
    }


    // удаление пользователя
    @GetMapping("/admin/delete/{username}")
    public String confirmDelete(@PathVariable("username") String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("roles", roleServce.index());
        return "/delete";
    }

    @DeleteMapping( "/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }


    //  показ старницы профиля для админа
    @GetMapping("/admin/user/{username}")
    public String showUser(Model model, Principal principal, @PathVariable("username") String name) {
        model.addAttribute("user", userService.findByUsername(name));
        model.addAttribute("roles", roleServce.index());
        return "/user";
    }

    //  показ старницы профиля для админа
    @GetMapping("/user")
    public String pageForReadProfile(Principal principal, Model model) {
        UserDto user = userService.findByUsername(principal.getName());
        model.addAttribute("roles", roleServce.index());
        model.addAttribute("user", user);
        return "/user";
    }

}
