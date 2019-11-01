package com.example.ad.search.impl;

import com.alibaba.fastjson.JSON;
import com.example.ad.index.CommonStatus;
import com.example.ad.index.DataTable;
import com.example.ad.index.adunit.AdUnitIndex;
import com.example.ad.index.adunit.AdUnitObject;
import com.example.ad.index.creative.CreativeIndex;
import com.example.ad.index.creative.CreativeObject;
import com.example.ad.index.creativeunit.CreativeUnitIndex;
import com.example.ad.index.district.UnitDistrictIndex;
import com.example.ad.index.interest.UnitItIndex;
import com.example.ad.index.keyword.UnitKeywordIndex;
import com.example.ad.search.ISearch;
import com.example.ad.search.vo.SearchRequest;
import com.example.ad.search.vo.SearchResponse;
import com.example.ad.search.vo.feature.DistrictFeature;
import com.example.ad.search.vo.feature.FeatureRelation;
import com.example.ad.search.vo.feature.ItFeature;
import com.example.ad.search.vo.feature.KeywordFeature;
import com.example.ad.search.vo.media.AdSlot;
import com.example.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class SearchImpl implements ISearch {


    @Override
    public SearchResponse fetchAds(SearchRequest request) {
        //获取请求的广告位信息
        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();

        //获取三个Feature
        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        ItFeature itFeature = request.getFeatureInfo().getItFeature();

        FeatureRelation relation = request.getFeatureInfo().getRelation();

        //构造响应对象
        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlot2Ads = response.getAdSlot2Ads();

        //开始根据入参依次过滤
        for (AdSlot adSlot : adSlots) {
            Set<Long> targetUnitIdSet;

            //根据流量类型获取初始 adUnit
            Set<Long> adUnitIdSet = DataTable.of(AdUnitIndex.class).match(adSlot.getPositionType());

            //如果关键词，地域，兴趣是and的关系
            if(relation == FeatureRelation.AND) {
                filterDistrictFeature(adUnitIdSet,districtFeature);
                filterItFeature(adUnitIdSet,itFeature);
                filterKeyWordFeature(adUnitIdSet,keywordFeature);

                targetUnitIdSet = adUnitIdSet;

                //如果关键词，地域，兴趣是or的关系
            } else {
                targetUnitIdSet = getORRelationUnitIds(
                        adUnitIdSet,
                        keywordFeature,
                        districtFeature,
                        itFeature
                );
            }

            //通过初步过滤出来的targetUnitIdSet来获取所有的AdUnitObject
            List<AdUnitObject> unitObjects = DataTable.of(AdUnitIndex.class).fetch(targetUnitIdSet);

            //把推广单元状态和推广计划状态无效的过滤出去
            filterAdUnitAndPlanStatus(unitObjects, CommonStatus.VALID);

            //然后通过广告创意单元索引找到所有的广告创意id
            List<Long> adIds = DataTable.of(CreativeUnitIndex.class).selectAds(unitObjects);
            List<CreativeObject> creativeObjects = DataTable.of(CreativeIndex.class).fetch(adIds);

            //通过adSlots实现对creativeObjects的过滤
            filterCreativeByAdSlot(
                    creativeObjects,
                    adSlot.getWidth(),
                    adSlot.getHeight(),
                    adSlot.getType()
            );

            adSlot2Ads.put(adSlot.getAdSlotCode(),buildCreativeResponse(creativeObjects));
        }
        log.info("fetchAds: {}-{}",
                JSON.toJSONString(request),
                JSON.toJSONString(response.getAdSlot2Ads()));
        return response;

    }

    private List<SearchResponse.Creative> buildCreativeResponse(
            List<CreativeObject> creativeObjects) {
        if(CollectionUtils.isEmpty(creativeObjects)) {
            return Collections.emptyList();
        }
        CreativeObject randomCreative = creativeObjects.
                get(Math.abs(new Random().nextInt()) % creativeObjects.size());
        return Collections.singletonList(SearchResponse.convert(randomCreative));
    }

    private void filterCreativeByAdSlot(List<CreativeObject> creativeObjects,
                                        Integer width,
                                        Integer height,
                                        List<Integer> type) {
        if(CollectionUtils.isEmpty(creativeObjects)) {
            return;
        }
        CollectionUtils.filter(
                creativeObjects,
                object -> object.getHeight().equals(height)
                && object.getWidth().equals(width)
                && object.getAuditStatus().equals(CommonStatus.VALID.getStatus())
                && type.contains(object.getType())
        );

    }

    private void filterAdUnitAndPlanStatus(List<AdUnitObject> unitObjects,
                                           CommonStatus status) {
        if(CollectionUtils.isEmpty(unitObjects)) {
            return;
        }
        CollectionUtils.filter(
                unitObjects,
                object -> object.getAdPlanObject().getPlanStatus()
                        .equals(status.getStatus()) && object.getUnitStatus()
                        .equals(status.getStatus())
        );
    }

    private Set<Long> getORRelationUnitIds(Set<Long> adUnitIdSet,
                                           KeywordFeature keywordFeature,
                                           DistrictFeature districtFeature,
                                           ItFeature itFeature) {
        if(CollectionUtils.isEmpty(adUnitIdSet)) {
            return Collections.emptySet();
        }
        Set<Long> keywordUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> districtUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> itUnitIdSet = new HashSet<>(adUnitIdSet);

        filterKeyWordFeature(keywordUnitIdSet, keywordFeature);
        filterDistrictFeature(districtUnitIdSet, districtFeature);
        filterItFeature(itUnitIdSet, itFeature);

        return new HashSet<>(
                CollectionUtils.union(
                        CollectionUtils.union(keywordUnitIdSet,districtUnitIdSet),itUnitIdSet)
        );
    }

    private void filterKeyWordFeature(Collection<Long> adUnitIds,
                                      KeywordFeature keywordFeature) {
        if(CollectionUtils.isEmpty(adUnitIds)) {
            return;
        }
        if(CollectionUtils.isNotEmpty(keywordFeature.getKeywords())) {
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitKeywordIndex.class)
                            .match(adUnitId,keywordFeature.getKeywords())
            );
        }
    }

    private void filterDistrictFeature(Collection<Long> adUnitIds,
                                       DistrictFeature districtFeature) {
        if(CollectionUtils.isEmpty(adUnitIds)) {
            return;
        }
        if(CollectionUtils.isNotEmpty(districtFeature.getDistricts())) {
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitDistrictIndex.class)
                            .match(adUnitId,districtFeature.getDistricts()));
        }
    }

    private void filterItFeature(Collection<Long> adUnitIds,
                                 ItFeature itFeature) {
        if(CollectionUtils.isEmpty(adUnitIds)) {
            return;
        }
        if(CollectionUtils.isNotEmpty(itFeature.getIts())) {
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitItIndex.class)
                            .match(adUnitId,itFeature.getIts())
            );
        }
    }
}
