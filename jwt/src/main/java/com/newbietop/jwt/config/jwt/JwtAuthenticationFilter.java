package com.newbietop.jwt.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

//스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
//login 요청해서 username,password전송하면(post)
//UsernamePasswordAuthenticationFilter 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;
	
	// /login요청을 하면 로그인 시도를 위해서 실행되는 함수
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter로그인 시도중");
		
		//1.username, password받아서
		//2.정상인지 로그인 시도를 해보는 것이다.authenticationManager로 로그인하면 PrincipalDetailsService loadUserByUsername 호출
		//3.PrincipalDetails를 세션에 담고(세션에 안담으면 권한관리가 안된다)
		//4.JWT토큰을 만들어서 응답해줌
		return super.attemptAuthentication(request, response);
	}
}
