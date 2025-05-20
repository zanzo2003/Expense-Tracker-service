package com.bhaskarshashwath.expense.tracker.modal.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequestDTO {

    /*
     * When a user's access token is expired he will user this DTO send refresh token for validation of session
     * then will get the new access token
     */
    private String token;
}
