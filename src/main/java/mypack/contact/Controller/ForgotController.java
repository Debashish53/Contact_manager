package mypack.contact.Controller;

import java.util.Random;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mypack.contact.Entities.User;
import mypack.contact.Service.EmailService;
import mypack.contact.dao.UserRepository;

@Controller
public class ForgotController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bcyBCryptPasswordEncoder;

	Random random = new Random();
	//email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) throws AddressException {
		
		System.out.println(email);
		
		int otp = random.nextInt(999999);
		System.out.println(otp);
		String subject="OTP from SCM";
		String message="<h1> OTP ="+otp+"</h1>";
		
		boolean flag = this.emailService.sendEmail(subject, message, email);
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			session.setAttribute("message", "Please enter correct OTP");
			return "forgot_email_form";
		}
		
	}
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myOtp==otp) {
			User user = this.userRepository.getUserByUserName(email);
			if(user==null) {
				session.setAttribute("message", "User does not exist!!1");
				return "forgot_email_form";
			}else {
				
			}
			return "password_change_form";
		}else {
			session.setAttribute("message", "You have enterd wrong otp !!!");
			return "verify_otp";
		}
	}
	//change password
	@PostMapping("/change-password")
	public String chnagePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		String email=(String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bcyBCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changed successfully...";
	}
}
