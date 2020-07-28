package com.avengereug.mall.common.constants;

public class ProductConstant {

    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "基础属性", "base"), ATTR_TYPE_SALE(0, "销售属性", "sale");

        AttrEnum(int code, String message, String alias) {
            this.code = code;
            this.message = message;
            this.alias = alias;
        }

        private int code;
        private String message;
        private String alias;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getAlias() {
            return alias;
        }
    }

}
