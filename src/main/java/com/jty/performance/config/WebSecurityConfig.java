package com.jty.performance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @Author: yeting
 * @Date: 2019/5/14 19:47
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler performanceAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler performanceAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/authentication/require")
                .loginProcessingUrl("/authentication/form")
                .successHandler(performanceAuthenticationSuccessHandler)
                .failureHandler(performanceAuthenticationFailureHandler)
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .and()
                .and()
                .authorizeRequests()
                .antMatchers("/authentication/require")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable();


    }
}
