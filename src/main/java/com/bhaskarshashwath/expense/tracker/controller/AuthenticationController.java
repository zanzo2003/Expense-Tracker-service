package com.bhaskarshashwath.expense.tracker.controller;


import com.bhaskarshashwath.expense.tracker.entity.RefreshToken;
import com.bhaskarshashwath.expense.tracker.model.request.AuthRequestDTO;
import com.bhaskarshashwath.expense.tracker.model.request.RefreshTokenRequestDTO;
import com.bhaskarshashwath.expense.tracker.model.response.ApiResponseDTO;
import com.bhaskarshashwath.expense.tracker.model.response.JwtResponseDTO;
import com.bhaskarshashwath.expense.tracker.service.JwtService;
import com.bhaskarshashwath.expense.tracker.service.RefreshTokenService;
import com.bhaskarshashwath.expense.tracker.util.ControllerHelper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ControllerHelper controllerHelper;

    @PostMapping("auth/v1/login")
    public ResponseEntity<ApiResponseDTO> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequest){

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());
            log.info("Authentication successful for user {}", authRequest.getUsername());
            return ResponseEntity.ok(
                    controllerHelper.createSuccessResponse(
                            "authentication successful",
                            JwtResponseDTO.builder()
                                    .accessToken(jwtService.generateToken(authRequest.getUsername()))
                                    .token(refreshToken.getToken())
                                    .build()
                    )
            );

        }catch (AuthenticationException e) {
            log.info("Authentication Exception : ", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(controllerHelper.createErrorResponse("authentication failed", e.getMessage()));
        }catch (Exception e){
            log.info("General Exception :", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(controllerHelper.createErrorResponse("Server is busy. Please try again", e.getMessage()));
        }
    }



    @PostMapping("auth/v1/refresh-token")
    public ResponseEntity<ApiResponseDTO> refreshToken(@NonNull @RequestBody RefreshTokenRequestDTO refreshTokenRequest){
        try{
            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshTokenRequest.getToken());
            if(!refreshTokenOptional.isPresent()){
                throw new RuntimeException("Refresh Token is not in DB");
            }
            RefreshToken refreshToken = refreshTokenOptional.get();
            return ResponseEntity.ok(controllerHelper
                    .createSuccessResponse("refresh token valid",
                            refreshTokenService.verifyRefreshToken(refreshToken)));

        }catch (RuntimeException e) {
            log.info("Authentication Exception : ", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(controllerHelper.createErrorResponse("authentication failed", e.getMessage()));
        }catch (Exception e){
            log.info("Generic Exception : ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(controllerHelper.createErrorResponse("Server is busy. Please try again", e.getMessage()));
        }
    }


}
