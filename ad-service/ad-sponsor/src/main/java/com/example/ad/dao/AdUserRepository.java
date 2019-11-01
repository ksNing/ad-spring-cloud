package com.example.ad.dao;

import com.example.ad.entity.AdUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdUserRepository extends JpaRepository<AdUser,Long> {
    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    AdUser findByUsername(String username);
}
