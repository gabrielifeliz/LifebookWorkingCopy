package com.lifebook.Controllers;

import com.lifebook.Model.AppUser;
import com.lifebook.Repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AppUserRepository users;

    @RequestMapping("/")
    public String homePageAdmin() {
        return "index";
    }

	@RequestMapping("/displayusers")
	public String showAllUsers(Model model) {
		model.addAttribute("users", users.findAll());
		return "displayusers";
	}
	@RequestMapping("/availability/{id}")
	public String suspend(@PathVariable("id") long id, Model model) {
		AppUser user = users.findById(id).get();
		user.setEnabled(!user.isEnabled());
		users.save(user);
		model.addAttribute("users", users.findAll());
		return "displayusers";
	}

	/*@RequestMapping("/unsuspend/{userId}")
	public String unsuspend(@PathVariable("id") long id, Model model) {
		AppUser user = users.findById(id).get();
		user.setEnabled(true);
		users.save(user);
		model.addAttribute("users", users.findAll());
		return "displayusers";
	}*/

}