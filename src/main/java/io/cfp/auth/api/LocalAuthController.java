/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.auth.api;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.cfp.auth.entity.User;
import io.cfp.auth.service.EmailingService;
import io.cfp.auth.service.ReCaptchaService;

@Controller
public class LocalAuthController extends AuthController {
	
	@Autowired
	private ReCaptchaService recaptchaService;
	
	@Autowired
	private EmailingService emailService;
	
	@RequestMapping(value = "/noEmail", method = RequestMethod.GET)
	public String noEmail(HttpServletResponse response, Map<String, Object> model) throws IOException {
		model.put("error", "noEmail");
		return "login";
	}

	@RequestMapping(value = "/local/login", method = RequestMethod.POST)
	public String login(HttpServletResponse response, @RequestParam String email, @RequestParam String password, Map<String, Object> model,
						@CookieValue(required = true, value = "returnTo") String returnTo) throws IOException {
		User user = userService.findByemail(email);

		if (user == null || user.getPassword() == null) {
			model.put("error", "invalidAuth");
			return "login";
		}

		if (!BCrypt.checkpw(password, user.getPassword())) {
			model.put("error", "invalidAuth");
			return "login";
		}
		
		return processUser(response, user.getEmail(), returnTo);
	}
	
	@RequestMapping(value = "/local/signup", method = RequestMethod.GET)
	public String signup(Map<String, Object> model) throws IOException {
		model.put("recaptchaKey", recaptchaService.getRecaptchaKey());
		return "signup";
	}
	
	@RequestMapping(value = "/local/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, HttpServletResponse response, @RequestParam String email, @RequestParam(name="g-recaptcha-response") String recaptcha, Map<String, Object> model) throws IOException {

		if (!recaptchaService.isCaptchaValid(recaptcha)) {
			model.put("error", "invalidCaptcha");
			return signup(model);
		}
		
		User user = userService.findByemail(email);
		
		if (user == null) {
			user = new User();
			user.setEmail(email);
		}
		
		user.setVerifyToken(UUID.randomUUID().toString());
		userService.save(user);
		
		emailService.sendEmailValidation(user, request.getLocale());
		
		return "redirect:/local/emailSent";
	}
	
	@RequestMapping(value = "/local/emailSent", method = RequestMethod.GET)
	public String emailSent() throws IOException {
		return "emailSent";
	}	
	
	@RequestMapping(value = "/local/register", method = RequestMethod.GET)
	public String register(@RequestParam String email, @RequestParam String token, Map<String, Object> model) {
		model.put("email", email.replaceAll("\"", ""));
		model.put("token", token.replaceAll("\"", ""));
		return "register";
	}
	
	@RequestMapping(value = "/local/register", method = RequestMethod.POST)
	public String register(HttpServletResponse response, @RequestParam String email, @RequestParam String password, @RequestParam String token, Map<String, Object> model,
						   @CookieValue(required = true, value = "returnTo") String returnTo) throws IOException {
		User user = userService.findByemail(email);
		
		if (user == null) {
			model.put("error", "invalidEmail");
			return register(email, token, model);
		}
		
		if (!token.equals(user.getVerifyToken())) {
			model.put("error", "invalidToken");
			return register(email, token, model);
		}
		
		user.setVerifyToken(null);
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		userService.save(user);
		
		return processUser(response, email, returnTo);
	}
	
}
