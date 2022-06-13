package com.appointment.api;

import com.appointment.api.service.AuthFilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration

@OpenAPIDefinition(info = @Info(title = "Clinic Appointment API", version = "1.0", description = "Make appointments in a simple way", license = @License(name = "Tell:+2507888", url = "https://digitalumuganda.com/"), contact = @Contact(url = "https://myclinic.com/", name = "Clinic", email = "ask@clinic.com")))

@SpringBootApplication

@EnableJpaAuditing
public class clinicApplication {
    public static void main(String[] args) {
        SpringApplication.run(clinicApplication.class, args);
    }

    @Configuration
    public class CorsConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {

            registry.addMapping("/**").allowedOrigins("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                    .allowCredentials(true);

        }

    }

    @Bean
    public FilterRegistrationBean<AuthFilter> filterRegistrationBean() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        AuthFilter authFilter = new AuthFilter();
        registrationBean.setFilter(authFilter);
        registrationBean.addUrlPatterns(

                "/api/v1/User/setrole/*",
                "/api/v1/User/update/*",
                "/api/v1/appointment/*",
                "/api/v1/User/logout");
        return registrationBean;
    }
}