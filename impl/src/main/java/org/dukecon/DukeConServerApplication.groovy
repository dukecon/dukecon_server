package org.dukecon

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan ("org.dukecon.server")
@EnableAutoConfiguration
class DukeConServerApplication {

    static void main(String[] args) {
        SpringApplication.run DukeConServerApplication, args
    }
}
