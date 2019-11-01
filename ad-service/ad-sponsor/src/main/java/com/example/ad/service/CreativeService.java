package com.example.ad.service;

import com.example.ad.vo.CreativeRequest;
import com.example.ad.vo.CreativeResponse;
import org.springframework.stereotype.Service;


public interface CreativeService {
    CreativeResponse createCreative(CreativeRequest request);
}
