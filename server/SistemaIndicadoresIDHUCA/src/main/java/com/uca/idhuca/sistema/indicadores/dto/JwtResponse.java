package com.uca.idhuca.sistema.indicadores.dto;

//dto/JwtResponse.java
public class JwtResponse {
 private String accessToken;
 private String refreshToken;
 private String tokenType = "Bearer";

 public JwtResponse(String accessToken, String refreshToken) {
     this.accessToken = accessToken;
     this.refreshToken = refreshToken;
 }

 // getters
 public String getAccessToken() { return accessToken; }
 public String getRefreshToken() { return refreshToken; }
 public String getTokenType() { return tokenType; }
}
