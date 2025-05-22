package com.bhaskarshashwath.expense.tracker.model.request;


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
     * When a user's access token is expired he will use this DTO send refresh token for validation of session
     * then will get the new access token
     */
    private String token;
}
