package com.example.messenger.controller;

import com.example.messenger.dto.*;
import com.example.messenger.repository.model.User;
import com.example.messenger.service.ReportService;
import com.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;
    private final UserFormValidator userFormValidator;
    private final ReportService reportService;

    public UserController(UserService userService, UserFormValidator userFormValidator, ReportService reportService) {
        this.userService = userService;
        this.userFormValidator = userFormValidator;
        this.reportService = reportService;
    }

    @InitBinder("userForm")
    private void initBinder(WebDataBinder webDataBinder) {
        if (webDataBinder.getTarget() != null && webDataBinder.getTarget().getClass() == UserForm.class) {
            webDataBinder.setValidator(userFormValidator);
        }
    }

    @GetMapping("/sign-up")
    public ModelAndView registration(ModelAndView model) {
        model.addObject("userForm", new UserForm());
        model.setViewName("sign-up");
        return model;
    }

    @PostMapping("/sign-up")
    public ModelAndView userRegistrationSubmit(ModelAndView model, @ModelAttribute("userForm") @Valid UserForm userForm, BindingResult result) {
        if (result.hasErrors()) {
            model.setViewName("sign-up");
            return model;
        }

        try {
            userService.register(userForm);
            model.addObject("email", userForm.getEmail());
            model.setViewName("mail-confirmation");
        } catch (IllegalArgumentException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("sign-up");
        } catch (RuntimeException e) {
            model.addObject("error", "Не удалось завершить регистрацию: " + e.getMessage());
            model.setViewName("sign-up");
        }

        return model;
//        if (result.hasErrors()) {
//            model.setViewName("sign-up");
//            return model;
//        }
//        else {
//            System.out.print("user registration");
//            User newUser = userService.register(userForm);
//            model.addObject("email", userForm.getEmail());
//            model.setViewName("mail-confirmation");
//            return model;
//        }
    }

    @GetMapping("/confirm-email")
    public ModelAndView validateEmail(ModelAndView model, @RequestParam String token) {
        Optional<User> user = userService.checkEmailToken(token);
        if (user.isEmpty()) {
            model.setViewName("mail-not-confirmed");
            return model;
        }
        model.setViewName("mail-confirmed");
        return model;
    }

    @GetMapping("/login")
    public ModelAndView loginPage(ModelAndView model) {
        model.setViewName("login");
        return model;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> profile(@PathVariable("userId") Long id, Principal principal) {
        User me = userService.getByEmail(principal.getName());
        ProfileResponse resp = userService.getProfile(id, me.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/forgot-password")
    public ModelAndView forgotPasswordPage(ModelAndView model) {
        model.setViewName("forgot-password");
        return model;
    }

    @PostMapping("/forgot-password")
    public ModelAndView forgotPasswordEmail(ModelAndView model, @RequestParam("email") String email) {
        userService.createPasswordResetToken(email);
        model.setViewName("forgot-password");
        return model;
    }

    @GetMapping("/reset-password")
    public ModelAndView resetPasswordPage(ModelAndView model, @RequestParam("token") String token) {
        Optional<User> user = userService.checkEmailTokenPR(token);
        if (user.isEmpty()) {
            return model;
        }
        model.addObject("token", token);
        model.setViewName("reset-password");
        return model;
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(ModelAndView model, @RequestParam("password") String newPassword, @RequestParam("token") String token) {
        userService.changePassword(newPassword, token);
        model.setViewName("login");
        return model;
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponse>> search(@RequestParam String query, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        return ResponseEntity.ok(userService.getAllByUsername(query, user.getId()));
    }

    @PostMapping("/change-username")
    public ModelAndView changeUsername(Principal principal, @RequestParam("username") String newUsername, ModelAndView model) {
        try {
            userService.changeUsername(principal.getName(), newUsername);
            model.addObject("success", "Имя пользователя успешно изменено!");
        } catch (IllegalArgumentException e) {
            model.addObject("error", e.getMessage());
        }
        User updatedUser = userService.findByEmail(principal.getName());
        model.addObject("profile", updatedUser);
        model.setViewName("profile");
        return model;
    }

    @PostMapping("/change-firstname")
    public String changeFirstname(Principal principal, @RequestParam("firstname") String newFirstname, ModelAndView model) {
        userService.changeFirstname(principal.getName(), newFirstname);
        return "redirect:/profile";
    }

    @PostMapping("/change-secondname")
    public String changeSecondname(Principal principal, @RequestParam("secondname") String newSecondname, ModelAndView model) {
        userService.changeSecondname(principal.getName(), newSecondname);
        return "redirect:/profile";
    }



    @PostMapping("/send-report/{message_id}")
    public ResponseEntity<String> sendReport(Principal principal, @PathVariable("message_id") Long messageId) {
        reportService.create(principal.getName(), messageId);
        return ResponseEntity.ok("report created");
    }

    @GetMapping("/profile")
    public ModelAndView profile(ModelAndView model, Principal principal) {
        MyProfileResp profile = userService.getMyProfile(principal.getName());
        model.addObject("profile", profile);
        model.setViewName("profile");
        return model;
    }
}
