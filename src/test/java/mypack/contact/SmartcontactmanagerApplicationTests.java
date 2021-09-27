package mypack.contact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import mypack.contact.Entities.User;
import mypack.contact.dao.UserRepository;

@SpringBootTest
class SmartcontactmanagerApplicationTests {
	
	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
	}
	@Test
	public void addUser() {
		User u=new User();
		u.setId(1);
		u.setName("Name2");
		u.setPassword("Password");
		u.setAbout("About");
		u.setEmail("email");
		u.setEnabled(true);
		u.setImgurl("url");
		u.setRole("role2");
		u.setContacts(null);
		userRepository.save(u);
		
	}
	@Test
	public void updateUser() {
		User usertoupdate = userRepository.findById(1).get();
		usertoupdate.setPassword("password");
		userRepository.save(usertoupdate);
	}
	@Test
	public void readAllUser() {
		List<User> alluser = userRepository.findAll();
		assertThat(alluser).size().isGreaterThan(0);
	}
	
	
	

}
