package io.cfp.auth;

import io.cfp.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Main controller
 */
@Controller
public class MainCtrl {

	@Autowired
	private TokenService tokenSrv;

	@RequestMapping("/")
	public String main(@CookieValue(value = "token", required = false) String token, HttpServletResponse response) {
		response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
		response.setHeader("Pragma","no-cache");
		response.setDateHeader("Expires", 0);

		if (!tokenSrv.isValid(token)) {
			return "login";
		}

		return "home";
	}
	
	@RequestMapping("/logout")
	public String logout(@CookieValue(value = "token", required = false) String token, HttpServletResponse response) {
		
		Cookie tokenCookie = new Cookie("token", token);
		tokenCookie.setMaxAge(0);
		tokenCookie.setPath("/");
		response.addCookie(tokenCookie);
		
		tokenSrv.remove(token);

		return "redirect:/";
	}
}
