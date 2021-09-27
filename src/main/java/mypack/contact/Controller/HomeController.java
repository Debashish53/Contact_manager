package mypack.contact.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import mypack.contact.Entities.User;
import mypack.contact.dao.UserRepository;
import mypack.contact.helper.Messages;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	@RequestMapping("/about")
	public String about() {
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,@RequestParam(value="agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session) {
		
		
		try {
			if(!agreement) {
				System.out.println("You have not agreed to the conditions.");
				throw new Exception("You have not agreed to the conditions.");
			}
			boolean test=bindingResult.hasFieldErrors();
			System.out.println(test);
			
			if(test) {
				System.out.println("ERROR"+bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImgurl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println(agreement);
			System.out.println(user);
			this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Messages("Successfully Registered!!!","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Messages("Something went wrong!!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
	}
	
	@RequestMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}
}
