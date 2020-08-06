package com.avengereug.mall.common.constants;

public final class ProductConstant {

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

    public enum AttrSearchTypeEnum {
        SUPPORT(1, "支持搜索"), UN_SUPPORT(0, "销售属性");

        AttrSearchTypeEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }


    public enum PublishStatusEnum {
        NEW(0, "新建"), UP(1, "上架"), DOWN(2, "下架");

        PublishStatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

    }

}
