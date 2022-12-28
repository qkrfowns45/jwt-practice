package com.newbietop.jwt.config.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbietop.jwt.config.auth.PrincipalDetails;
import com.newbietop.jwt.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
		try {
//			BufferedReader br = request.getReader();
//			
//			String input = null;
//			while((input = br.readLine()) != null) {
//				System.out.println(input);
//			}
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			System.out.println(user);
			
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			//PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨
			//DB에 있는 username과 password가 일치한다.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			//authentication 객체가 session영역에 저장됨
			PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
			System.out.println(principalDetails.getUser().getUsername()+"============="); //로그인이 정상적으로 되었다는 뜻
			
			//authentication 객체가 session영역에 저장을 해야하고 그 방법이 return 해주면 됨
			//리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는것
			//굳이 JWT토큰을 사용하면서 세션을 만들 이유가 없다. 단지 권한때쿤에 session에 넣어줌
			return authentication;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication함수가 실행됨
	//JWT 토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response해주면 됨
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행됨 : 인증이 완료되었다는 뜻");
		PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();
		
		//RSA방식은 아닌 Hash암호방식
		String jwtToken = JWT.create()
				.withSubject("newbietop토큰")
				.withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512("newbietop"));
		
		response.addHeader("Authorizaion", "Bearer "+jwtToken);
		//유저네임, 패스워드 로그인 정상
		//서버쪽 세션ID생성 클라이언트 쿠키 세션ID를 응답
		//요청할 때마다 쿠키값 세션ID를 항상 들고 서버쪽으로 요청하기 때문에 서버는 세션ID가 유효한지 판단해서 유효하면 인증이 필요한 페이지로 접근하면 됨
		//요청할 때마다 JWT토큰을 가지고 요청
		//서버는 JWT토큰이 유효한지를 판단해야해서 판단하는 필터를 만들예정
	}
}
