package org.bookie.test;

import org.bookie.conf.AppConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(AppConfiguration.class)
public class TestConfiguration {

}
