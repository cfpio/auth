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

package io.cfp.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.cfp.auth.service.CookieService;
import io.cfp.auth.service.TokenService;

/**
 * Main controller
 */
@Controller
public class MainCtrl {

	@Autowired
	private TokenService tokenSrv;
	
	@Autowired
	private CookieService cookieService;

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
		
		Cookie tokenCookie = cookieService.getTokenCookie("");
		tokenCookie.setMaxAge(0);
		response.addCookie(tokenCookie);
		
		Cookie redirectCookie = new Cookie("target", "");
		redirectCookie.setMaxAge(0);
		response.addCookie(redirectCookie);
		
		tokenSrv.remove(token);

		return "redirect:/";
	}
}
