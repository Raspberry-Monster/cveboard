package dev.raspberrykan.cveboard.security;

import dev.raspberrykan.cveboard.services.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutUrl("/user/logout"))
                .securityContext(context -> context.securityContextRepository(securityContextRepository()))
                .sessionManagement(session -> session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/user/register", "/user/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagge-ui", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/projects").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.PUT, "/projects/*").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.POST, "/projects/*/dependencies").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.PUT, "/projects/*/dependencies").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.DELETE, "/projects/*/dependencies/*").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.DELETE, "/projects/*").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.POST, "/dependencies").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.DELETE, "/dependencies/*").hasAuthority("Administrator")
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(PasswordHasher passwordHasher) {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return passwordHasher.hash(rawPassword == null ? null : rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return passwordHasher.matches(rawPassword == null ? null : rawPassword.toString(), encodedPassword);
            }
        };
    }
}
