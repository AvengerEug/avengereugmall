package com.avengereug.mall.order.params;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryParams {

    private Integer pageSize;
    private Integer currentPage;
}
