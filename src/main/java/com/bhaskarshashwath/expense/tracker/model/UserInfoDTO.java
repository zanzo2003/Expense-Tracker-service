package com.bhaskarshashwath.expense.tracker.model;

import com.bhaskarshashwath.expense.tracker.entity.UserInfo;

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
