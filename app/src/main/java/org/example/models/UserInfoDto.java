package org.example.models;

import org.example.entities.UserInfo;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoDto extends UserInfo {

    private String username;
    private String pasword;
    private String email;
    private Long phoneNumber;

}
