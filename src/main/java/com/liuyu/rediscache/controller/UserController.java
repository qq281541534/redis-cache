package com.liuyu.rediscache.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.liuyu.rediscache.entity.User;
import com.liuyu.rediscache.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list")
	public String list(HttpServletRequest request, Model model) {
		List<User> users = userService.list();
		model.addAttribute("users", users);
		return "list";
	}
	
	@RequestMapping("/details")
	public String details(HttpServletRequest request, @RequestParam String id) {
		User user = userService.findById(id);
		return "redirect:/list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Model model, String id) {
		if(id != null && !"".equals(id)){
			User user = userService.findById(id);
			model.addAttribute("user", user);
		}
		return "edit";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, User user) {
		if(user != null && user.getId() != null){
			userService.updateUser(user);
		} else {
			userService.addUser(user);
		}
		return "redirect:/list";
	}

	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String del(HttpServletRequest request, @RequestParam String id) {
		userService.deleteUser(id);
		return "redirect:/list";
	}


}
