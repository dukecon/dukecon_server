package org.dukecon.server.core

import groovy.util.logging.Slf4j

import org.dukecon.DukeConServerApplication;
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer


@Slf4j
class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		log.debug ("Configuring ServletInitializer")
		application.sources(DukeConServerApplication)
	}

}
