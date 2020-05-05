package com.sachetan.configurations;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.sachetan.Sachetan;

public class WebInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Sachetan.class);
	}

}