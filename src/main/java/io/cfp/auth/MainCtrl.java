package io.cfp.auth;

import io.cfp.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String main(HttpServletResponse response, @CookieValue(required=false) String token, @CookieValue(required=false) String target, @RequestParam(required=false, value="target") String targetParam) {
		response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
		response.setHeader("Pragma","no-cache");
		response.setDateHeader("Expires", 0);

		if (targetParam != null) {
			response.addCookie(new Cookie("target", targetParam));
			target = targetParam;
		}
		
		if (!tokenSrv.isValid(token)) {
			return "login";
		}
		
		if (target == null) {
			target = "http://www.cfp.io";
		}

		return "redirect:" + target;
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletResponse response, @CookieValue(required=false) String token) {
		
		Cookie tokenCookie = new Cookie("token", token);
		tokenCookie.setMaxAge(0);
		tokenCookie.setPath("/");
		response.addCookie(tokenCookie);
		
		tokenSrv.remove(token);

		return "redirect:/";
	}
}
