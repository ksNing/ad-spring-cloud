package com.example.ad.service;

import com.example.ad.Application;
import com.example.ad.vo.AdPlanGetRequest;
import com.example.ad.vo.exception.AdException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AdPlanServiceTest {

    @Autowired
    private AdPlanService planService;

    @Test
    public void testGetAdPlan() throws AdException {
        System.out.println(planService.getAdPlanByIds(
                new AdPlanGetRequest(15L,
                        Collections.singletonList(10L))
        ));
    }
}
