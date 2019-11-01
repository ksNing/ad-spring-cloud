package com.example.ad.service;

import com.example.ad.vo.CreateUserRequest;
import com.example.ad.vo.CreateUserResponse;
import com.example.ad.vo.exception.AdException;

public interface UserService {
    /**
     * <h2>创建用户</h2>
     */
    CreateUserResponse createUser(CreateUserRequest request)
            throws AdException;
}
