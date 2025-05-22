package com.bhaskarshashwath.expense.tracker.service;


import com.bhaskarshashwath.expense.tracker.entity.UserInfo;
import com.bhaskarshashwath.expense.tracker.model.UserInfoDTO;
import com.bhaskarshashwath.expense.tracker.repository.UserInfoRepository;
import com.bhaskarshashwath.expense.tracker.util.ValidationUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidationUtil validate;



    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserInfo userInfo = userRepository.findByUsername(username);
        if(userInfo == null){
            throw new UsernameNotFoundException(username + " not found in the database");
        }
        return new CustomUserDetails(userInfo);
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDTO userInfoDTO){
        return userRepository.findByUsername(userInfoDTO.getUsername());
    }


    public Boolean signUpUser(UserInfoDTO userInfoDTO){
        if(Objects.nonNull(checkIfUserAlreadyExists(userInfoDTO))){
            return false;
        }
        if( !validate.validateUserDetails(userInfoDTO) ){
            throw new IllegalArgumentException("User Details not valid according to policy.");
        }
        String encodedPassword = passwordEncoder.encode(userInfoDTO.getPassword());
        userRepository.save(
                UserInfo.builder()
                        .userId(UUID.randomUUID().toString())
                        .username(userInfoDTO.getUsername())
                        .password(encodedPassword)
                        .roles(new HashSet<>())
                        .build()
        );
        return true;
    }

}
