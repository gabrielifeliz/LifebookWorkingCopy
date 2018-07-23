package com.lifebook.Controllers;

import com.cloudinary.utils.ObjectUtils;
import com.lifebook.Model.AppUser;
import com.lifebook.Model.AppUserDetails;
import com.lifebook.Model.UserPost;
import com.lifebook.Repositories.*;
import com.lifebook.Service.CloudinaryConfig;
import com.lifebook.Service.FollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    AppRoleRepository roles;

    @Autowired
    AppUserDetailsRepository details;

    @Autowired
    FollowingService followingService;

    @Autowired
    UserPostRepository posts;

    @Autowired
    SettingRepository settings;

    @Autowired
    AppUserRepository users;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String homePageLoggedIn(Authentication authentication, Model model) {

        if (users.findByUsername(authentication.getName()).getRoles().contains(roles.findByRole("ADMIN"))) {
            return "redirect:/admin/";
        } else {
            Set<AppUser> following = users.findByUsername(authentication.getName())
                    .getDetail().getFollowingUsers();
            List<UserPost> posts = new ArrayList<>();
            for (AppUser u : following) {
                posts.addAll(u.getDetail().getPosts());
            }
            model.addAttribute("posts", posts);
            return "allposts";
        }
    }

    @PostMapping("/newmessage")
    public String sendMessage(@ModelAttribute("post") UserPost post,
                              @RequestParam("file") MultipartFile file, Authentication authentication) {
        AppUserDetails userDetails = users.findByUsername(authentication.getName()).getDetail();
        post.setCreator(userDetails);

        if (file.isEmpty()) {
            post.setImageUrl(null);
            posts.save(post);
            userDetails.getPosts().add(post);
            details.save(userDetails);
            return "redirect:/users/profile";
        }
        else {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                String uploadedName = (String) uploadResult.get("public_id");

                String transformedImage = cloudc.createUrl(uploadedName);
                post.setImageUrl(transformedImage);
                posts.save(post);
                userDetails.getPosts().add(post);
                details.save(userDetails);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/users/profile";
            }
        }

        return "redirect:/users/profile";
    }

    @RequestMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        AppUser user = users.findByUsername(authentication.getName());
        model.addAttribute("currentuser", user);
        //user.setDetail(new AppUserDetails());
        //Add information for the post form
        UserPost post = new UserPost();
        model.addAttribute("post",post);
        model.addAttribute("posts", posts.findAllByOrderByIdDesc());
        System.out.println(authentication.getName());
        return "profile";
    }

    @RequestMapping("/detail/{id}")
    public String showJob (@PathVariable("id") long id, Authentication auth) {

        AppUserDetails detail = details.findById(id).get();
        System.out.println(detail.getId());
        AppUser sessionUser = users.findByUsername(auth.getName());
        System.out.println(sessionUser.getDetail().getId());
        sessionUser.getDetail().getFollowingUsers().add(detail.getCurrentUser());
        for (AppUser u : sessionUser.getDetail().getFollowingUsers()) {
            System.out.println(u.getDetail().getId() + "--");
        }
        users.save(sessionUser);
        return "redirect:/users/profile";
    }

    @RequestMapping("/following")
    public String followingUsers() {
        return "following";
    }

    @RequestMapping("/weather")
    public String weather() {
        return "weather";
    }

    @RequestMapping("/news")
    public String news() {
        return "news";
    }

    @RequestMapping("/findpost")
    public String showResults(HttpServletRequest request, Model model) {
        model.addAttribute("posts", posts.findAllByContentContains(request.getParameter("query")));
        return "results";
    }
}