package com.bhaskarshashwath.expense.tracker.service;


import com.bhaskarshashwath.expense.tracker.entity.RefreshToken;
import com.bhaskarshashwath.expense.tracker.entity.UserInfo;
import com.bhaskarshashwath.expense.tracker.repository.RefreshTokenRepository;
import com.bhaskarshashwath.expense.tracker.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository tokenRepository;

    @Autowired
    private UserInfoRepository userRepository;

    public RefreshToken createRefreshToken(String username){
        UserInfo userDetails = userRepository.findByUsername(username);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .userInfo(userDetails)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return tokenRepository.save(newRefreshToken);
    }

    public RefreshToken verifyRefreshToken(RefreshToken token){
        if(token.getExpiryDate().isBefore(Instant.now())){
            tokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh Token is expired. Please Login again.");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token){
        return tokenRepository.findByToken(token);
    }

}
