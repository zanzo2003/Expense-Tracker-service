package com.bhaskarshashwath.expense.tracker.modal;

import com.bhaskarshashwath.expense.tracker.entities.UserInfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoDTO extends UserInfo {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

}
