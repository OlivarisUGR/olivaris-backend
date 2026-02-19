package com.olivaris.olivaris_app;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource("classpath:ValidationMessages.properties")
})
public class AppConfig {

}
