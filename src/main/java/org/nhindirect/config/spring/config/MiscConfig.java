package org.nhindirect.config.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class MiscConfig
{
	@Bean
	public StandardServletMultipartResolver multipartResolver()
	{
		return new StandardServletMultipartResolver();
	}
	
	@Bean 
	public ReloadableResourceBundleMessageSource messageSource()
	{
		final ReloadableResourceBundleMessageSource retVal = new ReloadableResourceBundleMessageSource();
		retVal.setBasename("/WEB-INF/messages");
		
		return retVal;
	}
}
