package com.avengereug.mall.es.service;


import com.avengereug.mall.es.to.SpuESTO;

import java.io.IOException;
import java.util.List;

public interface ESService {

    Boolean indexSpu(List<SpuESTO> spuESTOList) throws IOException;
}
