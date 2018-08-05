package com.github.eureka.example.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by Arvin.
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring()
                .mvcMatchers("/monitor/**", "/info", "/health", "/hystrix.stream");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // 要禁用，不然注册失败
        http.csrf().disable();
    }
}
