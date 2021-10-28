package com.arui.mall.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 全局统一返回结果类
 * @author ...
 */
@Data
@ApiModel(value = "全局统一返回结果")
public class R<T> {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data;

    private R(){}

    /**
     * 设置数据到 R
     * @param data
     * @param <T>
     * @return
     */
    protected static <T> R<T> data(T data) {
        R<T> r = new R<T>();
        if (data != null) {
            r.setData(data);
        }
        return r;
    }

    /**
     * 返回特定数据信息
     * @param body 数据体
     * @param retValCodeEnum code和msg
     * @param <T>
     * @return
     */
    public static <T> R<T> data(T body, RetValCodeEnum retValCodeEnum) {
        R<T> r = data(body);
        r.setCode(retValCodeEnum.getCode());
        r.setMessage(retValCodeEnum.getMessage());
        return r;
    }

    /**
     * 返回成功结果， data为null
     * @param <T>
     * @return
     */
    public static<T> R<T> ok(){
        return R.ok(null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static<T> R<T> ok(T data){
        R<T> r = data(data);
        return data(data, RetValCodeEnum.SUCCESS);
    }

    /**
     * 返回失败结果， data为null
     * @param <T>
     * @return
     */
    public static<T> R<T> error(){
        return R.fail(null);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> R<T> fail(T data){
        R<T> r = data(data);
        return data(data, RetValCodeEnum.FAIL);
    }


    /**
     * 设置特定信息
     * @param msg
     * @return
     */
    public R<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    /**
     * 设置特定code
     * @param code
     * @return
     */
    public R<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    /**
     * 判断当前状态是否为200
     * @return
     */
    public boolean isOk() {
        if(this.getCode().intValue() == RetValCodeEnum.SUCCESS.getCode().intValue()) {
            return true;
        }
        return false;
    }
}
