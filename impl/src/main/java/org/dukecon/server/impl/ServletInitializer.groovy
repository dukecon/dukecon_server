package org.dukecon.server.impl

import groovy.util.logging.Slf4j

import org.dukecon.DukeConServerApplication;
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer


@Slf4j
class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		log.debug ("Configuring ServletInitializer")
		application.sources(DukeConServerApplication)
	}

}
