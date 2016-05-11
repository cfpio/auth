package io.cfp.auth.api;

import com.github.scribejava.apis.LiveApi;
import com.github.scribejava.core.builder.api.DefaultApi20;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value="/microsoft")
public class MicrosoftAuthController extends Oauth20Controller {

    @Value("${cfp.microsoft.clientid}")
    private String clientId;
    
    @Value("${cfp.microsoft.clientsecret}")
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
	protected String getScope() {
		return "wl.emails";
	}

	@Override
	protected DefaultApi20 getApi() {
		return LiveApi.instance();
	}

	@Override
	protected String getEmailInfoUrl() {
		return "https://apis.live.net/v5.0/me?access_token=";
	}

	@Override
	protected String getEmailProperty() {
		return "emails.preferred";
	}
}
