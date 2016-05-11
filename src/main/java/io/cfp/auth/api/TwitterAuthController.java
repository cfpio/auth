
package io.cfp.auth.api;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth1RequestToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;


@Controller
@RequestMapping(value="/twitter")
public class TwitterAuthController extends OauthController {

    @Value("${cfp.twitter.clientid}")
    private String clientId;
    
    @Value("${cfp.twitter.clientsecret}")
    private String clientSecret;


	@Override
	protected String getClientId() {
		return clientId;
	}

	@Override
	protected String getClientSecret() {
		return clientSecret;
	}

	@Override
	protected DefaultApi10a getApi() {
		return TwitterApi.Authenticate.instance();
	}

	@Override
	protected String getEmailInfoUrl() {
		return "https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true&include_entities=false&skip_status=true";
	}

	@Override
	protected String getEmailProperty() {
		return "email";
	}
}
