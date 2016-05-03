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

package io.cfp.auth.api.provider;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import io.cfp.auth.api.OAuthController;


@Controller
public class GoogleAuthController extends OAuthController {

    private final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);

    @Value("${cfp.google.clientid}")
    private String clientId;
    @Value("${cfp.google.clientsecret}")
    private String clientSecret;
    @Value("${cfp.app.hostname}")
    private String hostname;
    
    private OAuth20Service service;
    
    @PostConstruct
    private void initService() {
    	 service = new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret).scope("https://www.googleapis.com/auth/userinfo.email").callback(hostname + "/auth/google").build(GoogleApi20.instance());	
    }

    @RequestMapping(value = "/login/google", method = RequestMethod.GET)
    public String loginGoogle() {
    	return "redirect:" + service.getAuthorizationUrl();
    }
    
    /**
     * Log in with Google
     *
     * @param httpServletResponse
     * @param httpServletRequest
     * @param info
     */
    @RequestMapping(value = "/auth/google", method = RequestMethod.GET)
    public String loginGoogle(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestParam String code) throws IOException {
    	OAuth2AccessToken accessToken = service.getAccessToken(code);
    	Map<String, Object> user = new RestTemplate().getForObject("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken.getAccessToken(), Map.class);
    	return processUser(httpServletResponse, (String) user.get("email"));
    }

    
}
