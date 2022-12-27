package com.newbietop.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbietop.jwt.model.User;

//CRUD 함수를 JpaRepository가 들고 있음
//@Repository라는 어노테이션이 없어도 ioc가 된다. 이유는 JpaRepository를 상속했기 때문
public interface UserRepository extends JpaRepository<User, Integer>{
	
	public User findByUsername(String username);

}
