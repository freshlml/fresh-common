package com.fresh.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 在JsonResult的基础上增加data节点，表示返回的数据
 * {
 *     "message": "SUCCESS",
 *     "code": "1",
 *     "success": true,
 *     "data": {  //返回的数据是一个对象
 *     }
 * }
 *
 * {
 *     "message": "SUCCESS",
 *     "code": "1",
 *     "success": true,
 *     "data": [  //返回的数据是一个列表
 *         {
 *         },
 *         {
 *         }
 *     ]
 * }
 *
 *
 * @param <T>
 */
@Deprecated
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class BasicJsonResult<T> extends JsonResult {
    protected BasicJsonResult() { }
    private T data;

    public T getData() {
        return data;
    }

    /*public BasicJsonResult<T> setData(T data) {
        this.data = data;
        return this;
    }*/
}
