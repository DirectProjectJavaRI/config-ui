package org.nhindirect.config.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MiscConfig
{
	@Bean
	public CommonsMultipartResolver multipartResolver()
	{
		return new CommonsMultipartResolver();
	}
	
	@Bean 
	public ReloadableResourceBundleMessageSource messageSource()
	{
		final ReloadableResourceBundleMessageSource retVal = new ReloadableResourceBundleMessageSource();
		retVal.setBasename("/WEB-INF/messages");
		
		return retVal;
	}
}
