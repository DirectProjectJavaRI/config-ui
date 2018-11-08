package org.nhindirect.config.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{
	@Value("${direct.configui.security.user.name}")
	protected String username;
	
	@Value("${direct.configui.security.user.password}")
	protected String password;
	
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception 
    {
        auth.inMemoryAuthentication()
          .withUser(username).password(passwordEncoder().encode(password)).roles("ADMIN");
    }	
	
    @Override
    public void configure(WebSecurity web) throws Exception 
    {
    	web.ignoring().antMatchers("/actuator/**");
    } 
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
    	http.csrf().disable();
    	
        http
        .authorizeRequests()
        .antMatchers("/resources/**").permitAll() 
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        //.loginProcessingUrl("/j_spring_security_check")
        .defaultSuccessUrl("/main", true)
        .failureUrl("/login?login_error=1")
        .permitAll()
        //.failureHandler(authenticationFailureHandler())
        .and()
        .logout()
        .logoutUrl("/login?logout=1")
        .deleteCookies("JSESSIONID");
        //.logoutSuccessHandler(logoutSuccessHandler());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    
}

