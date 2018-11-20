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
	private static final String BCRYPT_PREFIX = "{bcrypt}";
	
	@Value("${direct.configui.security.user.name}")
	protected String username;
	
	@Value("${direct.configui.security.user.password}")
	protected String password;
	
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception
    {
    	if (password.startsWith(BCRYPT_PREFIX))
    		password = password.substring(BCRYPT_PREFIX.length());  		
    	else
    		password = passwordEncoder().encode(password);

        auth
           .inMemoryAuthentication()
           .passwordEncoder(passwordEncoder())
           .withUser(username)
           .password(password)
           .roles("ADMIN");
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
        .defaultSuccessUrl("/main", true)
        .failureUrl("/login?login_error=1")
        .permitAll()
        .and()
        .logout()
        .logoutUrl("/logout")
        .deleteCookies("JSESSIONID")
        .logoutSuccessHandler((req,res,auth)->{   // Logout handler called after successful logout 
            req.getSession().setAttribute("message", "You are logged out successfully.");
            res.sendRedirect("login"); // Redirect user to login page with message.
         });
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    
}

