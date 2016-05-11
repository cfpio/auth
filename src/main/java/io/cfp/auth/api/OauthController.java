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

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class OauthController extends AuthController {

	private OAuth10aService authService;

	@PostConstruct
	private void init() {
		authService = new ServiceBuilder()
				.apiKey(getClientId())
				.apiSecret(getClientSecret())
				.callback(hostname + getProviderPath() + "/auth")
				.build(getApi());
	}

    @RequestMapping(value = "/login")
    public String login() {
		OAuth1RequestToken requestToken = authService.getRequestToken();
		return "redirect:" + authService.getAuthorizationUrl(requestToken);
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public String auth(HttpServletResponse httpServletResponse, @RequestParam("oauth_token") String token, @RequestParam("oauth_verifier") String verifier) throws IOException {
		final OAuth1RequestToken requestToken = new OAuth1RequestToken(token, "****");
		OAuth1AccessToken accessToken = authService.getAccessToken(requestToken, verifier);
		OAuthRequest request = new OAuthRequest(Verb.GET, getEmailInfoUrl(), authService);
		authService.signRequest(accessToken, request);
		Response response = request.send();

    	String email = response.getBody();
		// TODO extract email from json payload
    	return processUser(httpServletResponse, email);
    }

    protected String getProviderPath() {
		return this.getClass().getAnnotation(RequestMapping.class).value()[0];
	}
	
	protected abstract String getClientId();
	
	protected abstract String getClientSecret();
	
	protected abstract DefaultApi10a getApi();

	protected abstract String getEmailInfoUrl();
	
	protected abstract String getEmailProperty();
}
