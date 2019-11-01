package com.example.ad.controller;

import com.alibaba.fastjson.JSON;
import com.example.ad.client.SponsorClient;
import com.example.ad.client.vo.AdPlanGetRequest;
import com.example.ad.client.vo.Adplan;
import com.example.ad.search.ISearch;
import com.example.ad.search.vo.SearchRequest;
import com.example.ad.search.vo.SearchResponse;
import com.example.ad.vo.annotation.IgnoreResponseAdvice;
import com.example.ad.vo.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
public class SearchController {
    private ISearch search;
    private final RestTemplate restTemplate;

    @Autowired
    private SponsorClient sponsorClient;

    @Autowired
    public SearchController(RestTemplate restTemplate,
                            ISearch search) {
        this.restTemplate = restTemplate;
        this.search = search;
    }


    @PostMapping(value = "/fetchAds")
    public SearchResponse fetchAds(@RequestBody SearchRequest request) {
        log.info("fetchAds -> {}",
                JSON.toJSONString(request));
        return search.fetchAds(request);
    }
    @SuppressWarnings("all")
    @IgnoreResponseAdvice
    @PostMapping(value = "getAdPlanByRebbon")
    public CommonResponse<List<Adplan>> getAdPlanByRebbon(@RequestBody AdPlanGetRequest request) {
        log.info("ad-search getAdPlanByRebbon -> {} ",
                JSON.toJSONString(request));
        return restTemplate.postForEntity(
                "http://eureka-client-ad-sponsor/ad-sponsor/get/adPlan",
                request,
                CommonResponse.class
        ).getBody();

    }

    @IgnoreResponseAdvice
    @PostMapping(value = "getAdPlans")
    public CommonResponse<List<Adplan>> getAdPlanByFeign(@RequestBody AdPlanGetRequest request) {
        log.info("ad-search getAdPlanByFeign -> {}",
                JSON.toJSONString(request));
        return sponsorClient.getAdPlans(request);
    }
}
