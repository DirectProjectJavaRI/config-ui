package org.nhindirect.config.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration
{
	private static final String BCRYPT_PREFIX = "{bcrypt}";
	
	@Value("${direct.configui.security.user.name}")
	protected String username;
	
	@Value("${direct.configui.security.user.password}")
	protected String password;
	

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
    	
    	
    	if (password.startsWith(BCRYPT_PREFIX))
    		password = password.substring(BCRYPT_PREFIX.length());  		
    	else
    		password = passwordEncoder().encode(password);
    	
        UserDetails user = User.withUsername(username)
                .password(password)
                .roles("ADMIN")
                .build();
    	
        return new InMemoryUserDetailsManager(user);
    }

	/*
    @Override
    public void configure(WebSecurity web) throws Exception 
    {
    	web.ignoring().antMatchers("/actuator/**");
    } 
    */

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception
    {
    	http.csrf(csrf -> csrf.disable());
    	
    	http
    	  .authorizeHttpRequests(auth -> auth
    			  .requestMatchers("/main/**", "/domain/**", "/bundles/**", 
    			     "/certificates/**", "/dns/**", "/domain/**", "/policies/**", "/settings/**").authenticated()
    			  .requestMatchers("/resources/**", "/login", "**").permitAll()
    			  .anyRequest().authenticated()
    	   )
    	   .formLogin(form -> form
    			   .loginPage("/login")
    			   .defaultSuccessUrl("/main", true)
    			   .failureUrl("/login?login_error=1")
    			   .permitAll()
    	   )
    	   .logout(lout -> lout
    			   .logoutUrl("/logout")
    			   .deleteCookies("JSESSIONID")
    			   .logoutSuccessHandler((req,res,auth)->{   // Logout handler called after successful logout 
                       req.getSession().setAttribute("message", "You are logged out successfully.");
                       res.sendRedirect("login"); // Redirect user to login page with message.
                    })
    
    			   
    	   );

        return http.build();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    
}

