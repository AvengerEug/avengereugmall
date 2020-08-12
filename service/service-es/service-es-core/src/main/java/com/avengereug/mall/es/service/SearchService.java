package com.avengereug.mall.es.service;

import com.avengereug.mall.es.vo.SearchParam;
import com.avengereug.mall.es.vo.SearchResult;

public interface SearchService {

    SearchResult search(SearchParam param);
}
