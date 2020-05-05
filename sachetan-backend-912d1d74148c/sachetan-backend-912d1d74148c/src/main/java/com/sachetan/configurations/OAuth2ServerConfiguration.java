package com.sachetan.configurations;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.sachetan.services.SachetanUserDetailsService;

@Configuration
public class OAuth2ServerConfiguration {
	private static final String RESOURCE_ID = "sachetan_rest";
	private static final String CLIENT = "REST_API_CONSUMER";
	private static final String CLIENT_SECRET = "sachetan";

	// CLIENT:CLIENT_SECRET base64 encoded ->
	// UkVTVF9BUElfQ09OU1VNRVI6c2FjaGV0YW4=

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends
			ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			// @formatter:off
			resources
				.resourceId(RESOURCE_ID);
			// @formatter:on
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/api/v1/user").anonymous()
					.antMatchers(HttpMethod.GET, "/api/v1/incident/from-center").anonymous()
					.antMatchers(HttpMethod.GET, "/api/v1/incident/within-bounds").anonymous()					
					.antMatchers("/api/v1/admin/**").hasRole("ADMIN")
					.antMatchers(HttpMethod.POST, "/api/v1/incident-type").hasRole("ADMIN")
					.antMatchers("/api/v1/incident-type/**").authenticated()
					.antMatchers("/api/v1/user/**").authenticated()
					.antMatchers("/api/v1/incident/**").anonymous()
					.anyRequest().denyAll();
			// @formatter:on
		}
	}
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends
			AuthorizationServerConfigurerAdapter {

		private TokenStore tokenStore = new InMemoryTokenStore();

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Autowired
		private SachetanUserDetailsService userDetailsService;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			// @formatter:off
			endpoints
				.tokenStore(this.tokenStore)
				.authenticationManager(this.authenticationManager)
				.userDetailsService(userDetailsService);
			// @formatter:on
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients
				.inMemory()
					.withClient(CLIENT)
						.authorizedGrantTypes("password", "refresh_token")
						.authorities("USER", "ADMIN")
						.scopes("read", "write")
						.resourceIds(RESOURCE_ID)
						.secret(CLIENT_SECRET);
			// @formatter:on
		}

		@Bean
		@Primary
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setSupportRefreshToken(true);
			tokenServices.setTokenStore(this.tokenStore);
			return tokenServices;
		}

	}

}
