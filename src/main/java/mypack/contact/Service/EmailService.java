package mypack.contact.Service;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	JavaMailSender javaMailSender;
	public boolean sendEmail(String subject, String message, String to) {
		boolean f=false;
		String from="debashish952@gmail.com";
		try {
			//SimpleMailMessage m=new SimpleMailMessage();
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			//JavaMailSenderImpl m = new JavaMailSenderImpl();
			helper.setTo(to);
			helper.setFrom(from);
			helper.setSubject(subject);
			helper.setText(message, true);
			/*
			 * mim.setFrom(from); m.setTo(to); m.setSubject(subject); m.setText(message);
			 * 
			 * javaMailSender.send(m);
			 */
			Runnable cmd = () -> javaMailSender.send(mimeMessage);
			ThreadPoolExecutor ex = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
			ex.execute(cmd);	
			f=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
		
	}
}
