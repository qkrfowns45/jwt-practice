package com.newbietop.jwt.config.jwt;

public interface JwtProperties {
	String SECRET = "newbietop";
	int EXPIRATION_TIME = 60000*10;
	String TOKEN_PREFIX = "Bearer ";
	String HEADER_STRING = "Authorizion";
}
