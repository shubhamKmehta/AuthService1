package org.example.controller;

import org.example.entities.RefreshToken;
import org.example.request.AuthRequestDTO;
import org.example.request.RefreshTokenRequestDTO;
import org.example.response.JwtResponseDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

@Controller
public class TokenController {

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsServiceimpl;
    private final JwtService jwtService;
    public TokenController(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, UserDetailsServiceImpl userDetailsServiceimpl, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsServiceimpl = userDetailsServiceimpl;
        this.jwtService = jwtService;
    }

    @PostMapping("auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequestDTO.getUsername(),
                authRequestDTO.getPassword()
        ));

        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            String userId = userDetailsServiceimpl.getUserByUsername(authRequestDTO.getUsername());

            if(Objects.nonNull(userId) && Objects.nonNull(refreshToken)){
                return new ResponseEntity<>(JwtResponseDTO
                        .builder()
                        .accessToken(jwtService.generateToken(authRequestDTO.getUsername()))
                        .token(refreshToken.getToken())
                        .build(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Exception in user service",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("auth/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                  return JwtResponseDTO.builder()
                          .accessToken(accessToken)
                          .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(()-> new RuntimeException("RefreshToken is not in DB .. !!"));
    }
}
