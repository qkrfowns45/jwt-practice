package com.newbietop.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.newbietop.jwt.config.jwt.JwtAuthenticationFilter;
import com.newbietop.jwt.filter.MyFilter1;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsFilter corsFilter;
	
	@Autowired
	private CorsConfig corsConfig;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.addFilterBefore(new MyFilter1(),LogoutFilter.class);
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다.
		  .and()
		  .addFilter(corsFilter)  //@CrossOrigin(인증 x), 시큐리티 필터에 등록 인증(O)
		  .formLogin().disable()
		  .httpBasic().disable()
		  .apply(new MyCustomDsl())
		  .and()
		  .authorizeHttpRequests()
		  .requestMatchers("/api/v1/user/**").hasAnyRole("ROLE_USER","ROLE_MANAGER","ROLE_ADMIN")
		  .requestMatchers("/api/v1/manager/**").hasAnyRole("ROLE_MANAGER","ROLE_ADMIN")
		  .requestMatchers("/api/v1/admin/**").hasAnyRole("ROLE_ADMIN")
		  .anyRequest().permitAll();
		  
		
	    return http.build();
	}
	
	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
					.addFilter(corsConfig.corsFilter())
					.addFilter(new JwtAuthenticationFilter(authenticationManager));
		}
	}
}
