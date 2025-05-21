package com.bhaskarshashwath.expense.tracker.config;


import com.bhaskarshashwath.expense.tracker.repository.UserInfoRepository;
import com.bhaskarshashwath.expense.tracker.service.UserDetailsServiceImpl;
import com.bhaskarshashwath.expense.tracker.util.ValidationUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@Data
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private ValidationUtil validate;

    @Bean
    // this Bean will provide the custom user details service bean to the spring IOC which can
    // then be passed to other services. It will return the Impl object itself because userDetails is a interface
    public UserDetailsService userDetailsService(UserInfoRepository repository, PasswordEncoder passwordEncoder, ValidationUtil validate){
        return new UserDetailsServiceImpl(repository, passwordEncoder, validate);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .authorizeHttpRequests
                        (
                                auth -> auth
                                        .requestMatchers("auth/v1/login","auth/v1/refresh-token", "auth/v1/signup").permitAll()
                                        .anyRequest().authenticated()
                        )
                .sessionManagement
                        (
                        sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsPasswordService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
