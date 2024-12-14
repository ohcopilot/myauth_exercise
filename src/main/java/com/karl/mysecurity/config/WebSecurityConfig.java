package com.karl.mysecurity.config;

import com.karl.mysecurity.component.JwtAuthExceptEntryPoint;
import com.karl.mysecurity.component.MyAccessDeniedHandler;
import com.karl.mysecurity.filter.JwtAuthenticationTokenFilter;
import com.karl.mysecurity.service.MyUserDetailsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalAuthentication
public class WebSecurityConfig {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final MyUserDetailsManagerService userDetailsService;
    private final JwtAuthExceptEntryPoint jwtAuthExceptEntryPoint;
    private final MyAccessDeniedHandler myAccessDeniedHandler;

    @Autowired
    public WebSecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, MyUserDetailsManagerService userDetailsService,JwtAuthExceptEntryPoint jwtAuthExceptEntryPoint,MyAccessDeniedHandler myAccessDeniedHandler) {
        super();
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.userDetailsService = userDetailsService;
        this.jwtAuthExceptEntryPoint = jwtAuthExceptEntryPoint;
        this.myAccessDeniedHandler = myAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests
                            .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                            .requestMatchers("/auth/**","/public/**","/error").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                //.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)

                //.cors(Customizer.withDefaults())
                //.formLogin(AbstractHttpConfigurer::disable)
                //.httpBasic(AbstractHttpConfigurer::disable)
                /*.logout(logout->{
                    logout.logoutUrl("/auth/logout");
                })*/
                /*.formLogin(form->{
                    form
                            .loginPage("/auth/login.html")
                            .successHandler()
                            .loginProcessingUrl("/auth/login");
                })
                .logout(logout->{
                    logout
                            .logoutUrl("/auth/logout")
                            .logoutSuccessHandler();
                })
                 */
                //.userDetailsService(userDetailsService)
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(exception -> {
                    exception
                            .authenticationEntryPoint(jwtAuthExceptEntryPoint)
                            .accessDeniedHandler(myAccessDeniedHandler);
                })
                //.requestCache(RequestCacheConfigurer::disable)
                //.securityContext(context->context.securityContextRepository(new NullSecurityContextRepository()))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


/*
    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }*/
}