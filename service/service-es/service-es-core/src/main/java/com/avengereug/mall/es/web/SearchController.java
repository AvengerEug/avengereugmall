package com.avengereug.mall.es.web;

import com.avengereug.mall.es.service.SearchService;
import com.avengereug.mall.es.vo.SearchParam;
import com.avengereug.mall.es.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model) {
        SearchResult searchResult = searchService.search(param);
        model.addAttribute("result", searchResult);

        return "list";
    }

}
