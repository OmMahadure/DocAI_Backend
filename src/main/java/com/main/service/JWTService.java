package com.main.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JWTService {
	
	private final SecretKey key;
	
	public JWTService(@Value("${app.jwt.secret}")String secret) {
		 this.key = Keys.hmacShaKeyFor(//Converts those raw bytes into a cryptographically secure SecretKey object used to sign and verify tokens using the HS256 algorithm.
	                Decoders.BASE64.decode(secret)//Decodes your Base64-encoded secret key back into raw bytes.
	        );
	
}
	public String generateToken(Long userId, String email) {
		 long expiration = 1000L * 60 * 60 * 24; // 24 hour

	        return Jwts.builder()
	                .subject(email)
	                .claim("userId", userId)
	                .issuedAt(new Date())
	                .expiration(new Date(System.currentTimeMillis() + expiration))
	                .signWith(key)
	                .compact();
	    }

	    public Long extractUserId(String token) {
	        return Jwts.parser()
	                .verifyWith(key)
	                .build()
	                .parseSignedClaims(token)
	                .getPayload()
	                .get("userId", Long.class);
	    }

	    public String extractEmail(String token) {
	        return Jwts.parser()
	                .verifyWith(key)
	                .build()
	                .parseSignedClaims(token)
	                .getPayload()
	                .getSubject();
	    }

	}
	
	
