package no.nav.pam.ad.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {Application.class})
public class AppConfig {


    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}