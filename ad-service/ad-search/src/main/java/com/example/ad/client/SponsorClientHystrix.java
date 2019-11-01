package com.example.ad.client;

import com.example.ad.client.vo.AdPlanGetRequest;
import com.example.ad.client.vo.Adplan;
import com.example.ad.vo.vo.CommonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SponsorClientHystrix implements SponsorClient {
    @Override
    public CommonResponse<List<Adplan>> getAdPlans(AdPlanGetRequest request) {
        return new CommonResponse<>(-1,"eureka sponsor client error");
    }
}
