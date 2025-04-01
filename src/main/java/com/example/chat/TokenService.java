package com.example.chat;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
	private final JwtEncoder encoder;
	private final JwtDecoder decoder;

	public TokenService(JwtEncoder encoder, JwtDecoder decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	public Jwt generateToken(Authentication auth, Integer userId) {
		Instant now = Instant.now();
		String scope = auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(" "));
		JwtClaimsSet claims = JwtClaimsSet.builder()
			//this needs to be my url
			.issuer("http://localhost:7777")
			.issuedAt(now)
			.expiresAt(now.plus(1, ChronoUnit.HOURS))
			.subject(userId.toString())
			.claim("scope", scope)
			.build();
		return this.encoder.encode(JwtEncoderParameters.from(claims));
	}

	public boolean isNotExpired(String token) {
		var t = decoder.decode(token);
		Instant exp = t.getExpiresAt();
		if (Instant.now().minus(1, ChronoUnit.HOURS).isAfter(exp) ) {
			return false;
		}

		return true;
	}

	public int getUserId(String token) {
		var jwt = decoder.decode(token);
		return Integer.parseInt(jwt.getClaim("sub"));
	}

	//token is expired but user still logged in
	//I need 2 tokens for this. 1 short lived token and one long lived just to get the new short lived one.
	public String issueNew(String token) {
		return "";
	}
}
