package com.bhaskarshashwath.expense.tracker.modal.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDTO {
    /*
    This will be used when the user user tries to login
     */
    private String accessToken;
    private String token;
}
