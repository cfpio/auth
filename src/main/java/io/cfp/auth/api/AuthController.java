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

import io.cfp.auth.entity.User;
import io.cfp.auth.service.CookieService;
import io.cfp.auth.service.TokenService;
import io.cfp.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

public abstract class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	protected UserService userService;

	@Autowired
	protected TokenService tokenService;
	
	@Autowired
	private CookieService cookieService;
	
	protected RestTemplate restTemplate = new RestTemplate();
	
	@Value("${cfp.app.hostname}")
    protected String hostname;

	/**
	 * Return JWT token and eventually persist user according to providerId and
	 * provider
	 */
	protected String processUser(HttpServletResponse response, String email, String returnTo) {

		if (email == null) {
			return "redirect:/noEmail";
		}

		User user = userService.findByemail(email);

		if (user == null) {
			user = new User();
			user.setEmail(email);
			user = userService.save(user);
		}

		// add a token for the user
		String token = tokenService.create(email, user.isSuperAdmin());
		response.addCookie(cookieService.getTokenCookie(token));

		if (returnTo == null || returnTo.isEmpty()) {
			returnTo = "http://www.cfp.io";
		}

		logger.info("[LOGGED_IN] User logged in with [{}] and redirect to [{}]", getClass().getSimpleName(), returnTo);

		return "redirect:"+returnTo;
	}
}
