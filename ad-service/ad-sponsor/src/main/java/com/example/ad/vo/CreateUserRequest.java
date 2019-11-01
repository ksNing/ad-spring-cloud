package com.example.ad.vo;

import lombok.Data;
import org.springframework.util.StringUtils;
@Data
public class CreateUserRequest {

    private String username;

    public boolean validate() {

        return !StringUtils.isEmpty(username);
    }
}
