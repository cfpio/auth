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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import io.cfp.auth.api.AuthController;


@Controller
public class GithubAuthController extends AuthController {

    private final Logger log = LoggerFactory.getLogger(GithubAuthController.class);

    @Value("${cfp.github.clientid}")
    private String clientId;
    @Value("${cfp.github.clientsecret}")
    private String clientSecret;
    
    private OAuth20Service authService;
    
    @PostConstruct
    private void init() {
    	 authService = new ServiceBuilder()
    			 .apiKey(clientId).apiSecret(clientSecret)
    			 .scope("user:email")
    			 .callback(hostname + "/auth/github")
    			 .build(GitHubApi.instance());	
    }

    @RequestMapping(value = "/login/github")
    public String login() {
    	return "redirect:" + authService.getAuthorizationUrl();
    }
    
    @RequestMapping(value = "/auth/github", method = RequestMethod.GET)
    public String auth(HttpServletResponse httpServletResponse, @RequestParam String code) throws IOException {
    	OAuth2AccessToken accessToken = authService.getAccessToken(code);
    	Map<String, Object> user = restTemplate.getForObject("https://api.github.com/user?access_token=" + accessToken.getAccessToken(), Map.class);
    	return processUser(httpServletResponse, (String) user.get("email"));
    }

    
}
