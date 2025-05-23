package com.bhaskarshashwath.expense.tracker.controller;


import com.bhaskarshashwath.expense.tracker.entity.RefreshToken;
import com.bhaskarshashwath.expense.tracker.model.UserInfoDTO;
import com.bhaskarshashwath.expense.tracker.model.response.ApiResponseDTO;
import com.bhaskarshashwath.expense.tracker.model.response.JwtResponseDTO;
import com.bhaskarshashwath.expense.tracker.service.JwtService;
import com.bhaskarshashwath.expense.tracker.service.RefreshTokenService;
import com.bhaskarshashwath.expense.tracker.service.UserDetailsServiceImpl;
import com.bhaskarshashwath.expense.tracker.util.ControllerHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class RegistrationController {


    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ControllerHelper controllerHelper;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<ApiResponseDTO> signUp(@RequestBody UserInfoDTO userInfoDTO)
    {
        try{
            log.info("User Info DTO : {}", userInfoDTO);
            Boolean isSignedUp = userDetailsService.signUpUser(userInfoDTO);
            if(!isSignedUp){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(controllerHelper.createErrorResponse("user already exists", "user with current username already exists"));
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDTO.getUsername());
            String jwtToken = jwtService.generateToken(userInfoDTO.getUsername());
            return ResponseEntity.ok(controllerHelper
                    .createSuccessResponse("user registration completed for " + userInfoDTO.getUsername(),
                            JwtResponseDTO.builder().accessToken(jwtToken)
                            .token(refreshToken.getToken()).build()));

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(controllerHelper.createErrorResponse("Invalid input", e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(controllerHelper.createErrorResponse("User registration failed", e.getMessage()));
        }

    }



}
