package com.avengereug.mall.es.service.impl;

import com.avengereug.mall.es.service.ESService;
import com.avengereug.mall.es.service.SearchService;
import com.avengereug.mall.es.vo.SearchParam;
import com.avengereug.mall.es.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ESService esService;

    @Override
    public SearchResult search(SearchParam param) {

        return null;
    }
}
