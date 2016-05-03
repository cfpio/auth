package io.cfp.auth.api;

import io.cfp.auth.dto.ErrorRes;
import io.cfp.auth.dto.LoginReq;
import io.cfp.auth.dto.LoginRes;
import io.cfp.auth.entity.User;
import io.cfp.auth.repository.UserRepo;
import io.cfp.auth.service.TokenSrv;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Login a user
 */
@RestController
@RequestMapping("/api/tokens")
public class TokenCtrl {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private TokenSrv tokenSrv;

	@Value("${token.cookie-domain}")
	private String cookieDomain;


	@RequestMapping(value = "", method = POST)
	public LoginRes login(@RequestBody LoginReq req, HttpServletResponse response) throws FileNotFoundException {
		User user = userRepo.findByEmail(req.getEmail());

		if (user == null) {
			throw new FileNotFoundException("User/Pass invalid");
		}

		if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) {
			throw new FileNotFoundException("User/Pass invalid");
		}

		String email = user.getEmail();

		//add a token for the user
		String token = tokenSrv.create(email, user.isSuperAdmin());

		LoginRes res = new LoginRes();
		res.setToken(token);

		Cookie tokenCookie = new Cookie("token", token);
		tokenCookie.setPath("/");
		tokenCookie.setHttpOnly(true); //secure Token to be invisible from javascript in the browser
		tokenCookie.setDomain(cookieDomain);
		response.addCookie(tokenCookie);

		return res;
	}

	@RequestMapping(value = "/{token}", method = DELETE)
	@ResponseStatus(NO_CONTENT)
	public void logout(@PathVariable String token) throws FileNotFoundException {
		tokenSrv.remove(token);
	}

	@ExceptionHandler(FileNotFoundException.class)
	@ResponseStatus(NOT_FOUND)
	public ErrorRes handleFNFE(FileNotFoundException e) {
		return new ErrorRes("Not Found", e.getMessage());
	}

}
