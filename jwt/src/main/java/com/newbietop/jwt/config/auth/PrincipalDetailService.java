package com.newbietop.jwt.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.newbietop.jwt.model.User;
import com.newbietop.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

//http://localhost:8080/login =>여기서 동작을 안한다.
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		System.out.println("PrincipalDetailsService동작!!");
		User user = userRepository.findByUsername(username);
		return new PrincipalDetails(user);
	}

	
}
