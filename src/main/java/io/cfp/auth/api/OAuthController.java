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

import io.cfp.auth.dto.LoginRes;
import io.cfp.auth.entity.User;
import io.cfp.auth.service.TokenSrv;
import io.cfp.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public abstract class OAuthController {

	private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);

	private final String CONFLICT_MSG = "There is already a %s account that belongs to you";

	private final String NOT_FOUND_MSG = "User not found";

	@Autowired
	UserService userService;

	@Autowired
	TokenSrv tokenService;
	
	@Value("${token.cookie-domain}")
	private String cookieDomain;

	/**
	 * Return JWT token and eventually persist user according to providerId and
	 * provider
	 *
	 * @param httpServletResponse
	 * @param userInfos

	 */
	protected String processUser(HttpServletResponse response, String email) throws IOException {
		User user = userService.findByemail(email);

		if (user == null) {
			user = new User();
			user.setEmail(email);
			user = userService.save(user);
		}

		// add a token for the user
		String token = tokenService.create(email, user.isSuperAdmin());

		Cookie tokenCookie = new Cookie("token", token);
		tokenCookie.setPath("/");
		tokenCookie.setHttpOnly(true); // secure Token to be invisible from
										// javascript in the browser
		tokenCookie.setDomain(cookieDomain);
		response.addCookie(tokenCookie);

		return "redirect:/";
	}
}
