package com.w3lsolucoes.dscatalog.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    @Profile("test") // Essa configuração será aplicada apenas quando o perfil 'test' estiver ativo
    @Order(1) // Ordem de prioridade para garantir que essa configuração seja aplicada antes das outras
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathRequest.toH2Console()) // Aplica essa configuração apenas ao console H2
                .csrf(AbstractHttpConfigurer::disable) // Desabilita a proteção contra CSRF para o console H2
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // Permite que o H2 seja exibido em iframes

        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll() // Permite todas as requisições para todas as rotas
                        .anyRequest().authenticated() // Exige autenticação para outras requisições não especificadas
                )
                .csrf(AbstractHttpConfigurer::disable); // Desabilita CSRF se necessário

        return http.build();
    }
}
