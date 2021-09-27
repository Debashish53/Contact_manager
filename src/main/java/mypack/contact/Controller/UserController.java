package mypack.contact.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import mypack.contact.Entities.Contact;
import mypack.contact.Entities.User;
import mypack.contact.dao.ContactRepository;
import mypack.contact.dao.UserRepository;
import mypack.contact.Entities.PhoneNumbers;
import mypack.contact.helper.Messages;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println(userName);
		User user = userRepository.getUserByUserName(userName);
		System.out.println(user);
		model.addAttribute("user",user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	//contact adding handler
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("numbers") Integer [] numbers, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			List<PhoneNumbers> nums=new ArrayList<PhoneNumbers>();
			for(int i=0;i<numbers.length;i++) {
				PhoneNumbers p=new PhoneNumbers();
				p.setContact(contact);
				p.setNumbers(numbers[i]);
				nums.add(p);
			}
			contact.setNumbers(nums);
			if(file.isEmpty()) {
				contact.setImage("contact.png");
				
			}else {
				contact.setImage(file.getOriginalFilename());
				File saveFile=new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploaded");
			}
			if(contact.getImage()==null) {
				contact.setImage("contact.png");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			//System.out.println(contact);
			System.out.println("Contact added to database..");
			//success message
			session.setAttribute("message", new Messages("Contact Added Successfully!!!", "success"));
		} catch (Exception e) {
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			//error message
			session.setAttribute("message", new Messages("Something went wrong!!!", "danger"));
		}
		return "normal/add_contact_form";
	}
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show all contacts");
		String userName=principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contacts";
	}
	//particular contact detail
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact",contact);
		}
		model.addAttribute("title", "Contact Details");
		System.out.println(cId);
		return "normal/contact_detail";
	}
	//delete a contact
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		//contact.setUser(null);
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		session.setAttribute("message", new Messages("Contact deleted succesfully", "success"));
		
		return "redirect:/user/show-contacts/0";
	}
	//open update a contact form
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId, Model model) {
		model.addAttribute("title", "Update form");
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}
	//update a contact
	@RequestMapping(value="/process-update", method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
		
		try {
			Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile, oldContactDetails.getImage());
				file1.delete();
				
				File saveFile=new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldContactDetails.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Messages("Your contact is updated successfully","success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	//profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	//open settings handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Messages("Your password is changed successfully..","success"));
		}else {
			//error
			session.setAttribute("message", new Messages("Wrong old password!!!","danger"));
			return "redirect:/user/settings";
		}
		System.out.println(oldPassword);
		return "redirect:/user/index";
	}
	@PostMapping("/update-user/{id}")
	public String updateUserForm(@PathVariable("id") Integer uid, Model model) {
		User user = userRepository.findById(uid).get();
		model.addAttribute(user);
		return "normal/update_user_profile";
	}
	
	@PostMapping("/process-update-user")
	public String updateUser(@ModelAttribute User user, Model model, HttpSession session, Principal principal) {
		userRepository.save(user);
		return "redirect:/user/profile";
	}
	
}
