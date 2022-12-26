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
