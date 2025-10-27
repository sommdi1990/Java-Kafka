package com.kafka.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminServerProperties adminServer;

    public SecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(this.adminServer.path("/assets/**")).permitAll()
                .requestMatchers(this.adminServer.path("/actuator/**")).permitAll()
                .requestMatchers(this.adminServer.path("/instances")).permitAll()
                .requestMatchers(this.adminServer.path("/instances/**")).permitAll()
                .requestMatchers(this.adminServer.path("/login")).permitAll()
                .anyRequest().authenticated()
        ).formLogin(formLogin -> formLogin
                .loginPage(this.adminServer.path("/login"))
                .successHandler(successHandler)
        ).logout(logout -> logout
                .logoutUrl(this.adminServer.path("/logout"))
        ).httpBasic();

        return http.build();
    }
}
