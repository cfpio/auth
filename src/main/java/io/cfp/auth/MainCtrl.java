package io.cfp.auth;

import io.cfp.auth.service.TokenSrv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * Main controller
 */
@Controller
public class MainCtrl {

	@Autowired
	private TokenSrv tokenSrv;

	@RequestMapping("/")
	public String main(@CookieValue(value = "token", required = false) String token, HttpServletResponse response) {
		response.setHeader("Cache-Control","no-cache,no-store,must-revalidate");
		response.setHeader("Pragma","no-cache");
		response.setDateHeader("Expires", 0);

		if (tokenSrv.get(token) == null) {
			return "forward:login.html";
		}

		return "forward:home.html";
	}

	@RequestMapping("/logout")
	public String logout(@CookieValue(value = "token", required = false) String token) {
		tokenSrv.remove(token);

		return "redirect:./";
	}
}
