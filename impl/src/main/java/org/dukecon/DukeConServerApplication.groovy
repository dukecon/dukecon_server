package org.dukecon

import org.h2.server.web.WebServlet
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.filter.ShallowEtagHeaderFilter

import javax.servlet.Filter

@SpringBootApplication
@ComponentScan("org.dukecon.server")
@EnableCircuitBreaker
@EnableAutoConfiguration
class DukeConServerApplication {

    static void main(String[] args) {
        SpringApplication.run DukeConServerApplication, args
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public ServletRegistrationBean h2WebServlet() {
        def registration = new ServletRegistrationBean(new WebServlet(), '/console/*')
        return registration
    }

}


