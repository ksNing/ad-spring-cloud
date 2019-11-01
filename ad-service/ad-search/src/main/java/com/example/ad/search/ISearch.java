package com.example.ad.search;

import com.example.ad.search.vo.SearchRequest;
import com.example.ad.search.vo.SearchResponse;

public interface ISearch {
    SearchResponse fetchAds(SearchRequest request);
}
