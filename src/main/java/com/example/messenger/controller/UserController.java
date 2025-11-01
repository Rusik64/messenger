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

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        if (webDataBinder.getTarget() != null && webDataBinder.getTarget().getClass() == UserForm.class) {
            webDataBinder.setValidator(userFormValidator);
        }
    }

    @GetMapping("/sign-up")
    public ModelAndView registration(ModelAndView model) {
        model.addObject(new UserForm());
        model.setViewName("sign-up");
        return model;
    }

    @PostMapping("/sign-up")
    public ModelAndView userRegistrationSubmit(ModelAndView model, @ModelAttribute @Valid UserForm userForm, BindingResult result) {
        if(result.hasErrors()) {
            model.setViewName("sign-up");
            return model;
        }
        System.out.print("user registration");
        User newUser = userService.register(userForm);
        model.addObject("email", userForm.getEmail());
        model.setViewName("mail-confirmation");
        return model;
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
        User me = userService.getByUsername(principal.getName());
        ProfileResponse resp = userService.getProfile(id, me.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponse>> search(@RequestParam String query, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        return ResponseEntity.ok(userService.getAllByUsername(query, user.getId()));
    }

    @PostMapping("/change-username")
    public ResponseEntity<String> changeUsername(Principal principal, @RequestParam("username") String newUsername) {
        userService.changeUsername(principal.getName(), newUsername);
        return ResponseEntity.ok("username changed");
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
