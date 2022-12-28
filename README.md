# jwt-practice

## jwt란?
> 벨 연구소에서 처음으로 rfc문서(프로토콜)를 만들면서 그 후 만들어진 7510번째 문서이다. json web token이라고 불리며 확장성이 좋은 서버를 구성할 때 많이 사용하고 있다.
> jwt는 그 자체를 암호화하여 당사자 간에 비밀을 제공할 수 도 있지만 서명된 토큰에 중점을 둔다. json데이터를 Base64 URL-safe Encode를 통해 인코딩하며 위변조 방지를 위해
> 내부에는 전자 서명도 들어있다. 따라서 사용자가 jwt를 서버로 전송하면 서버는 서명을 검증하는 과정을 거치며 검증이 완료되면 요청한 응갑을 돌려준다.

> 구조는 Header, Payload, Signature로 구성되어 있으며 모습은 xxxxx.yyyyy.zzzzz이다. 처음부터 Header, Payload, Signature이며 Header에는 jwt에서 사용할 타입과 해시 알고리즘의
> 종류가 담겨져 있으며 Payload에는 서버에서 첨부한 사용자 권한 정보와 데이터가 담겨있다. 마지막으로 Signature 에는 Header, Payload 를 Base64 URL-safe Encode 를 한 이후 Header 에 > 명시된 해시함수를 적용하고, 개인키(Private Key)로 서명한 전자서명이 담겨있다. 자서명에는 비대칭 암호화 알고리즘을 사용하므로 암호화를 위한 키와 복호화를 위한 키가 다르다. 암호화(전> 자서명)에는 개인키를, 복호화(검증)에는 공개키를 사용한다.

>jwt는 토큰 인증 신뢰성을 가지는데 이유는 서버는 토큰 안에 들어있는 정보가 무엇인지 아는게 중요한 것이 아니라 해당 토큰이 유효한 토큰인지 확인하는 것이 중요하기 때문에, 클라이언트로부> 터 받은 JWT의 헤더, 페이로드를 서버의 key값을 이용해 시그니처를 다시 만들고 이를 비교하며 일치했을 경우 인증을 통과시킨다.(조작을 알고 방지할 수 있음!!)

## 2022-12-25
> jwt를 위한 프로젝트 및 yml세팅을 완료했다. 앞으로 jwt에 대한 공부 내용은 이쪽으로 담을 생각이고 인증과 허가에 대한 범용적인 방식인 jwt를 깊이 학습할 수 있었으면 좋겠다.

## 2022-12-26
> jwt에서는 세션에 저장하지 않고 고유 토큰을 가지고 인증과 응답을 처리한다. 그래서 securityconfig에서 세션을 사용하지 않도록
> sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)를 붙여주고 .httpBasic().disable()로 발생하는 인증절차를 다 disable처리한다.
> 그리고 filter를 만들어서 http.addFilterBefore(new MyFilter1(),LogoutFilter.class) 설정을 해주고 어떤 filter보다도 먼저 처리할 수 있게 작성했다. jwt는 세션을 사용하지 않다보니
> filter처리를 해서 인증을 관리해야하는 번거로움이 있지만 그만큼 안정성과 확장성 뛰어나다는 것을 알게되었다. 초반에만 고생하면 유지보수하기에도 편할것이다. 그전 OAuth공부하며
> 만들었던 수많은 로그인 세션들을 생각하면 이게 양반일수도 있다.

## 2022-12-27
> jwt로그인을 하기 위해 사전작업이 간단한줄 알았지만 filter설정에서 까다로운 부분들이 있었다. .apply(new MyCustomDsl()) securityConfig를 통해 AbstractHttpConfigurer를 extends
> 하여 만든 filter를 만들어준다. principaldetails나 principaldetailsservice를 만들어줬다(이 부분은 자주했던 부분이므로 설명 스킵)/login요청을 하면 로그인 시도를 위해서 실행되는 
> 함수인 attemptAuthentication에서 로그인을 시도하면 어떻게 반응하는지 테스트까지 진행했다. 많은 세팅부분이있어서 손이 많이 가는게 확실한거 같다. 어제는 간단하다고 생각했지만
> 그리 간단하지 않다는 것을 몇분만에 알게 되었다. 내일까지해서 이 jwt를 마무리할 생각이다.

## 2022-12-28
> 마지막으로 jwt토큰을 만들어서 최종적으로 서버를 구축해보았다. JwtAuthenticationFilter를 통해 인증을 완료하기 위해서 필터 안의 attemptAuthentication에서 DB에 있는 User정보와 비교> 비교하였다. attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication함수가 실행되는데 여기에 토큰을 만들어준다.

```
String jwtToken = JWT.create()
				.withSubject("newbietop토큰")
				.withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512("newbietop"));
        //임의로 만들어준 토큰이다 실제로는 소스에 있는거처럼 propertise를 만들어서 설정해주는 것이 좋다.
```
> 그 후 다음 필터인 JwtAuthorizaionFilter를 추가적으로 만들어서 login한 유저의 토큰이 도메인으로 접속할 때 ROLE을 잘 가지고 있는지를 토큰으로 비교한다. JwtAuthorizaionFilter
> 생성자를 만들고 doFilterInternal선언해서 인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 거친다. 
```
String jwtHeader = request.getHeader("Authorization");
		System.out.println(jwtHeader+"==============");
		
		//header가 있는지 확인
		if(jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
			chain.doFilter(request, response);
			return;
		}
		
		//jwt토큰을 검증해서 정상적인 사용자인지 확인
		String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
		
		String username = JWT.require(Algorithm.HMAC512("newbietop")).build().verify(jwtToken).getClaim("username").asString();
		
		//서명이 정상적으로 됨
		if(username != null) {
			User userEntity = userRepository.findByUsername(username);
			PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
			
			//Jwt 토큰 서명을 통해 서명이 정상이면 Authentication 객체를 만들어준다.
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails,null,principalDetails.getAuthorities());
			
			//강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(request, response);
		}
 ```
 >까지하면 요청한 도메인으로 해당 role을 가지고있으면 정상적으로 return하게 된다!! 이로써 전체적인 jwt토큰을 사용한 로그인 시스템을 구성해보았다. 나름 시간을 잡아먹었지만 시큐리티
 >와 jwt를 테스트해보기에는 적절한 시간이었다고 생각한다. 이제 앞서배운 OAuth와 jwt를 사용해서 react를 활용한 나만의 서버를 구축할 예정이다!

