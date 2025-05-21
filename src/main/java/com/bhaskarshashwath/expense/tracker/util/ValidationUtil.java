package com.bhaskarshashwath.expense.tracker.util;


import com.bhaskarshashwath.expense.tracker.modal.UserInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {

    public Boolean validateUserDetails(UserInfoDTO user){

        // password validation
        boolean isValidPassword = user.getPassword().length() > 8;

        // email validation
        boolean isValidEmail = user.getEmail().endsWith("@gmail.com") || user.getEmail().endsWith("@outlook.com") || user.getEmail().endsWith("hotmail.com");

        //phone number validation
        boolean isValidPhoneNumber = user.getPhoneNumber().length() == 10;

        return (isValidPassword && isValidEmail && isValidPhoneNumber);
    }
}
