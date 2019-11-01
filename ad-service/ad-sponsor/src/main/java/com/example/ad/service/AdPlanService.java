package com.example.ad.service;

import com.example.ad.entity.AdPlan;
import com.example.ad.vo.AdPlanGetRequest;
import com.example.ad.vo.AdPlanRequest;
import com.example.ad.vo.AdPlanResponse;
import com.example.ad.vo.exception.AdException;

import java.util.List;

public interface AdPlanService {
    /**
     * <h2>创建推广计划</h2>
     * */
    AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException;

    /**
     * <h2>获取推广计划</h2>
     * */
    List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException;

    /**
     * <h2>更新推广计划</h2>
     * */
    AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException;

    /**
     * <h2>删除推广计划</h2>
     * */
    void deleteAdPlan(AdPlanRequest request) throws AdException;
}
