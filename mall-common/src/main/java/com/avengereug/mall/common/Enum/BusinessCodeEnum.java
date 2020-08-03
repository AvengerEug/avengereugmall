package com.avengereug.mall.common.Enum;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 *
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 */
public enum BusinessCodeEnum {

    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),

    NOT_ALLOWED_MERGED_CAUSED_BY_ILLEGAL_PURCHASE_ID(14001, "purchase id 非法，不允许被合并"),
    NOT_ALLOWED_MERGED_CAUSED_BY_ILLEGAL_STATUS(14002, "采购单目前状态不允许被合并"),
    NOT_ASSIGNED_PURCHASE_CAUSED_BY_ILLEGAL_PURCHASE_STATUS(14003, "采购单状态非法，无法分配给采购人员"),
    NOT_ASSIGNED_PURCHASE_CAUSED_BY_NO_SAME_ASSIGNED(14004, "采购单领取者与采购单分配者不相同，无法领取"),
    NOT_ALLOWED_FINISHED_PURCHASE_CAUSED_BY_NOT_EXISTS_PURCHASE_DETAILS_ID(14005, "采购详情单不存在，无法完成采购单"),

    ;

    private int code;
    private String msg;

    BusinessCodeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }



}
