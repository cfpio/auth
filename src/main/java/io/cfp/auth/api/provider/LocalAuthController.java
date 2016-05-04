package io.cfp.auth.api.provider;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.cfp.auth.api.AuthController;
import io.cfp.auth.dto.LoginReq;
import io.cfp.auth.entity.User;

@Controller
public class LocalAuthController extends AuthController {

	@RequestMapping(value = "/login/local", method = RequestMethod.POST)
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
}
