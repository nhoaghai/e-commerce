package com.ecommerce.common.config;

import com.ecommerce.domain.security.jwt.AuthTokenFilter;
import com.ecommerce.domain.security.jwt.JwtAuthEntryPoint;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailServiceImpl userDetailService;
    private final JwtAuthEntryPoint unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    // public DaoAuthenticationProvider authenticationProvider(){ are the same
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // provider userDetailsService for DaoAuthenticationProvider
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager will use UserDetailsService to get the user from the database
    // also uses password encoder to encode and decode the password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        String[] swaggerUrl = {"/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs",
                "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
                "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
                "/api/test/**", "/authenticate"};

        String[] permitAllUrl = {"/api/v3/products/**", "/api/v3/categories/**"};
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exception -> exception.authenticationEntryPoint(unauthorizedHandler)))
                .sessionManagement((session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(swaggerUrl).permitAll()
                        .requestMatchers("/api/v3/auth/**").permitAll()
                        .requestMatchers(permitAllUrl).permitAll()
                        .requestMatchers("/api/v3/member/**").hasAnyRole("MEMBER", "SELLER","ADMIN")
                        .requestMatchers("/api/v3/seller/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/v3/admin/**").hasRole("ADMIN")

                );
        httpSecurity.authenticationProvider(authenticationProvider());

        httpSecurity.addFilterBefore(authTokenFilter,
                UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
