package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.models.UserInfoDto;
import org.example.response.JwtResponseDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserDetailsService userDetailsService;

    public AuthController(JwtService jwtService, RefreshTokenService refreshTokenService, UserDetailsServiceImpl userDetailsServiceImpl, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.userDetailsService = userDetailsService;
    }


    @PostMapping("auth/v1/signUp")
    public ResponseEntity signUp(@RequestBody UserInfoDto userInfoDto){
        try{
            String userId = userDetailsServiceImpl.singUpUser(userInfoDto);
            if(Objects.isNull(userId)){
                return new ResponseEntity<>("Already Exist",HttpStatus.BAD_REQUEST);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.generateToken(userInfoDto.getUsername());
            return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken).token(refreshToken.getToken()).userId(userId).build(),HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Exception in user Service",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("auth/v1/ping")
    public ResponseEntity<String> ping(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()){
            String userId = userDetailsServiceImpl.getUserByUsername(authentication.getName());
            if(Objects.nonNull(userId)){
                return ResponseEntity.ok(userId);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");

    }

    @GetMapping("/health")
    public ResponseEntity<Boolean> checkHealth(){
        return new ResponseEntity<>(true,HttpStatus.OK);
    }
}
