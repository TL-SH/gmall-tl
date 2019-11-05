package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;

/**
 * @author tanglei
 */
public interface SearchService {

    SearchResponse search(SearchParamVO searchParamVO);
}
