package com.avengereug.mall.common.anno;

import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public @interface GlobalTransactional {
}
