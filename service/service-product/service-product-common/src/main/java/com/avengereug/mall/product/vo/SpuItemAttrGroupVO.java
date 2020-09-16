package com.avengereug.mall.product.vo;

import com.avengereug.mall.product.vo.spusave.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuItemAttrGroupVO {

    private String groupName;

    private List<Attr> attrs;

}
