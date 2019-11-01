package com.example.ad.client;

import com.example.ad.client.vo.AdPlanGetRequest;
import com.example.ad.client.vo.Adplan;
import com.example.ad.vo.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "eureka-client-ad-sponsor",
        fallback = SponsorClientHystrix.class)
public interface SponsorClient {

    @RequestMapping(value = "/ad-sponsor/get/adPlan",
            method = RequestMethod.POST)
    CommonResponse<List<Adplan>> getAdPlans(@RequestBody AdPlanGetRequest request);

}
