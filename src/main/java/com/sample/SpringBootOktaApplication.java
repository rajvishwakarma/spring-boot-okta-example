package com.sample;

import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.okta.spring.config.OktaOAuth2Properties;

@EntityScan(basePackages = { "com.sample.*" })
@ComponentScan(basePackages = { "com.sample.*" })
@EnableJpaRepositories(basePackages = { "com.sample.*" })
@SpringBootApplication
@EnableOAuth2Sso
public class SpringBootOktaApplication {
	
	private final Logger logger = LoggerFactory.getLogger(SpringBootOktaApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootOktaApplication.class, args);
	}
	
	/**
     * Enable the use of {@link org.springframework.security.access.prepost.PreAuthorize PreAuthorize} annotation
     * and OAuth expressions like {code}#oauth2.hasScope('email'){code}.
     */
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class GlobalSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            return new OAuth2MethodSecurityExpressionHandler();
        }
    }

    /**
     * Create an ApplicationListener that listens for successful logins and simply just logs the principal name.
     * @return a new listener
     */
    @Bean
    protected ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessEventApplicationListener() {
        return event -> logger.info("Authentication Success with principal: {}", event.getAuthentication().getPrincipal());
    }

    @Bean
    protected Filter oktaSsoFilter(ApplicationEventPublisher applicationEventPublisher,
                                   OAuth2ClientContext oauth2ClientContext,
                                   AuthorizationCodeResourceDetails authorizationCodeResourceDetails,
                                   ResourceServerTokenServices tokenServices,
                                   OktaOAuth2Properties oktaOAuth2Properties) {

        // There are a few package private classes the configure a OAuth2ClientAuthenticationProcessingFilter, in order
        // to change how the login redirect works we need to copy a bit of that code here
        OAuth2ClientAuthenticationProcessingFilter oktaFilter = new OAuth2ClientAuthenticationProcessingFilter(oktaOAuth2Properties.getRedirectUri());
        oktaFilter.setApplicationEventPublisher(applicationEventPublisher);
        OAuth2RestTemplate oktaTemplate = new OAuth2RestTemplate(authorizationCodeResourceDetails, oauth2ClientContext);
        oktaFilter.setRestTemplate(oktaTemplate);
        oktaFilter.setTokenServices(tokenServices);
        return oktaFilter;
    }

    @Configuration
    @Order(99) // Must be less then 100 in order to configure before OAuth2SsoDefaultConfiguration
    static class OAuth2SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final Filter oktaSsoFilter;

        private final OktaOAuth2Properties oktaOAuth2Properties;

        OAuth2SecurityConfigurerAdapter(Filter oktaSsoFilter, OktaOAuth2Properties oktaOAuth2Properties) {
            this.oktaSsoFilter = oktaSsoFilter;
            this.oktaOAuth2Properties = oktaOAuth2Properties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                // add our SSO Filter in place
                .addFilterAfter(oktaSsoFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(oktaOAuth2Properties.getRedirectUri()))
                    .accessDeniedHandler((req, res, e) -> res.sendRedirect("/403"))

                // allow anonymous users to access the root page
                .and()
                    .authorizeRequests()
                        .antMatchers("/", "/login", "/css/**").permitAll()
                        .antMatchers("/**").authenticated()

                // send the user back to the root page when they logout
                .and()
                    .logout().logoutSuccessUrl("/");
        }
    }
}
