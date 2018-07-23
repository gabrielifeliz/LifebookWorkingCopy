package com.lifebook.Controllers;

import com.lifebook.Model.*;
import com.lifebook.Repositories.*;
import com.lifebook.Service.CloudinaryConfig;
import com.lifebook.Service.StmpMailSender;
import com.lifebook.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    AppRoleRepository roles;

    @Autowired
    AppUserDetailsRepository details;

    @Autowired
    UserService userService;

    @Autowired
    UserPostRepository posts;

    @Autowired
    SettingRepository settings;

    @Autowired
    AppUserRepository users;

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    private StmpMailSender stmpMailSender;

    @RequestMapping("/")
    public String homePage() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registration(Model model){
        model.addAttribute("user", new AppUser());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(
            @Valid @ModelAttribute("user") AppUser user,
            BindingResult result, HttpServletRequest request, Model model) throws MessagingException {
        model.addAttribute("user", user);

        System.out.println(result.getAllErrors().toString());

        if (result.hasErrors()) {
            if (users.findByEmail(user.getEmail()) != null) {
                model.addAttribute("existingemail",
                        "There is an account with that email: " + user.getEmail());
            }

            if (users.findByUsername(user.getUsername()) != null) {
                model.addAttribute("existingusername",
                        "There is an account with that username: " + user.getUsername());
            }
            return "registration";
        }

        // Disable user until they click on confirmation link in email
        user.setEnabled(false);
        // Generate random 36-character string token for confirmation link
        user.setConfirmationToken(UUID.randomUUID().toString());

        userService.save(user);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
        stmpMailSender.sendEmailMessage(user.getEmail(), "Registration Confirmation",
                "To confirm your e-mail address, please click the link below:\n"
                + appUrl + "/confirm?token=" + user.getConfirmationToken());
        model.addAttribute("confirmationMessage",
                "A confirmation e-mail has been sent to " + user.getEmail());
        return "registration";
    }
    
    @GetMapping("/confirm")
    public String confirmRegistration(Model model, @RequestParam("token") String token) {

        AppUser user = userService.findByConfirmationToken(token);

        if (user == null) { // No token found in DB
            model.addAttribute("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else { // Token found
            model.addAttribute("confirmationToken", user.getConfirmationToken());
        }

        return "confirm";
    }

    @PostMapping("/confirm")
    public String confirmRegistration(Model model, @RequestParam Map<String, String> requestParams) {

        // Find the user associated with the reset token
        AppUser user = userService.findByConfirmationToken(requestParams.get("token"));

        String passwordInput = requestParams.get("password");
        // Set new password
        if (passwordInput.isEmpty() || passwordInput.equalsIgnoreCase(null)) {
            model.addAttribute("errorMessage", "Please provide a valid password");
            return "redirect:/confirm?token="+ user.getConfirmationToken();
        } else if (passwordInput.length() < 4) {
            model.addAttribute("errorMessage", "Please provide a strong password");
            return "redirect:/confirm?token="+ user.getConfirmationToken();
        }

        user.setPassword(new BCryptPasswordEncoder().encode(passwordInput));

        // Set user to enabled
        user.setEnabled(true);

        // Save user
        userService.saveUser(user);

        model.addAttribute("successPasswordMessage", "Your password has been set!");
        model.addAttribute("successMessage", "Your account has been activated!");


        return "confirm";
    }

    @PostConstruct
    public void loadData() {

        AppRole admin = new AppRole();
        admin.setRole("ADMIN");
        roles.save(admin);

        AppUser adminLogin = new AppUser();
        adminLogin.setUsername("admin");
        adminLogin.setPassword(new BCryptPasswordEncoder().encode("adminp"));
        adminLogin.setEmail("lifebookapplication@gmail.com");
        adminLogin.setFirstName("An");
        adminLogin.setLastName("Administrator");
        adminLogin.setEnabled(true);
        adminLogin.getRoles().add(admin);
        users.save(adminLogin);

    }
}