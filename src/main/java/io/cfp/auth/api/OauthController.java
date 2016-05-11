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
