package io.cfp.auth.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.cfp.auth.dto.LoginReq;
import io.cfp.auth.entity.User;
import io.cfp.auth.service.EmailingService;

@Controller
public class LocalAuthController extends AuthController {
	
	@Autowired
	private EmailingService emailService;

	@RequestMapping(value = "/local/login", method = RequestMethod.POST)
	public String login(@RequestBody LoginReq req, HttpServletResponse response) throws IOException {
		User user = userService.findByemail(req.getEmail());

		if (user == null) {
			throw new FileNotFoundException("User/Pass invalid");
		}

		if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) {
			throw new FileNotFoundException("User/Pass invalid");
		}
		return processUser(response, user.getEmail());
	}
	
	@RequestMapping(value = "/local/signup", method = RequestMethod.GET)
	public String signup() throws IOException {
		return "signup";
	}
	
	@RequestMapping(value = "/local/signup", method = RequestMethod.POST)
	public String signup(HttpServletResponse response, @RequestParam String email) throws IOException {
		User user = userService.findByemail(email);
		
		//TODO add captcha
		
		if (user == null) {
			user = new User();
			user.setEmail(email);
		}
		
		user.setVerifyToken(UUID.randomUUID().toString());
		userService.save(user);
		
		emailService.sendEmailValidation(user, Locale.FRENCH); //TODO locale
		
		return "redirect:/local/emailSent";
	}
	
	@RequestMapping(value = "/local/emailSent", method = RequestMethod.GET)
	public String emailSent() throws IOException {
		return "emailSent";
	}	
	
	@RequestMapping(value = "/local/register", method = RequestMethod.GET)
	public String register(@RequestParam String email, @RequestParam String token, Map<String, Object> model) {
		model.put("email", email);
		model.put("token", token);
		return "register";
	}
	
	@RequestMapping(value = "/local/register", method = RequestMethod.POST)
	public String register(HttpServletResponse response, @RequestParam String email, @RequestParam String password, @RequestParam String token, Map<String, Object> model) throws IOException {
		User user = userService.findByemail(email);
		
		if (user == null) {
			model.put("error", "invalidEmail");
			return "register";
		}
		
		if (!token.equals(user.getVerifyToken())) {
			model.put("error", "invalidToken");
			return "register";
		}
		
		user.setVerifyToken(null);
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		userService.save(user);
		
		return processUser(response, email);
	}
	
}
