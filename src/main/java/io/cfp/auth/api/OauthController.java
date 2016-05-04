package io.cfp.auth.api;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

public abstract class OauthController extends AuthController {

	private OAuth20Service authService;

	@PostConstruct
	private void init() {
		authService = new ServiceBuilder()
				.apiKey(getClientId())
				.apiSecret(getClientSecret())
				.scope(getScope())
				.callback(hostname + getProviderPath() + "/auth")
				.build(getApi());
	}
	
	@RequestMapping(value = "/login")
    public String login() {
    	return "redirect:" + authService.getAuthorizationUrl();
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public String auth(HttpServletResponse httpServletResponse, @RequestParam String code) throws IOException {
    	OAuth2AccessToken accessToken = authService.getAccessToken(code);
    	Map<String, Object> user = restTemplate.getForObject(getEmailInfoUrl() + accessToken.getAccessToken(), Map.class);
    	return processUser(httpServletResponse, (String) user.get(getEmailProperty()));
    }
    
    protected String getProviderPath() {
		return this.getClass().getAnnotation(RequestMapping.class).value()[0];
	}
	
	protected abstract String getClientId();
	
	protected abstract String getClientSecret();
	
	protected abstract String getScope();
	
	protected abstract DefaultApi20 getApi();

	protected abstract String getEmailInfoUrl();
	
	protected abstract String getEmailProperty();
}
