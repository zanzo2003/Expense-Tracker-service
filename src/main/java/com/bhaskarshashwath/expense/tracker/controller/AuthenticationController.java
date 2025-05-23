package com.bhaskarshashwath.expense.tracker.controller;


import com.bhaskarshashwath.expense.tracker.entity.RefreshToken;
import com.bhaskarshashwath.expense.tracker.model.request.AuthRequestDTO;
import com.bhaskarshashwath.expense.tracker.model.response.ApiResponseDTO;
import com.bhaskarshashwath.expense.tracker.model.response.JwtResponseDTO;
import com.bhaskarshashwath.expense.tracker.service.JwtService;
import com.bhaskarshashwath.expense.tracker.service.RefreshTokenService;
import com.bhaskarshashwath.expense.tracker.util.ControllerHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

        }catch (Exception e) {
            log.info("Exception caught at AuthenticationController : ", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(controllerHelper.createErrorResponse("authentication failed", e.getMessage()));
        }
    }



}
