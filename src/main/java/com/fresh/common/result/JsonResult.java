package com.fresh.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fresh.common.enums.JsonResultEnum;

import java.util.List;

/**
 *基础json返回字段定义
 * 成功，返回如下
 * {
 *     "message": "SUCCESS",
 *     "code": "1",
 *     "success": true
 * }
 * 失败，返回如下
 * {
 *     "message": "错误message",
 *     "code": "错误code",
 *     "success": false
 * }
 *
 *data作为数据节点，表示返回的数据
 *  {
 *      "message": "SUCCESS",
 *      "code": "1",
 *      "success": true,
 *      "data": {  //返回的数据是一个对象
 *      }
 *  }
 *
 *  {
 *      "message": "SUCCESS",
 *      "code": "1",
 *      "success": true,
 *      "data": [  //返回的数据是一个列表
 *          {
 *          },
 *          {
 *          }
 *      ]
 *  }
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class JsonResult<T> {

    private boolean success;
    private String code;
    private String message;

    private T data;

    protected JsonResult() {}

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }


    public static <T> JsonResult<T> buildSuccessResult(T data) {
        JsonResult<T> result = new JsonResult<T>();
        result.setSuccess(true);
        result.setCode(JsonResultEnum.SUCCESS.getCode());
        result.setMessage(JsonResultEnum.SUCCESS.getText());
        result.setData(data);
        return result;
    }


    //for com.baomidou.mybatisplus.core.metadata.IPage
    public static <T> JsonResult<PageJsonResultVo<T>> buildSuccessResult(long current,
                                                    long size,
                                                    long pages,
                                                    long total,
                                                    List<T> records) {
        JsonResult<PageJsonResultVo<T>> result = new JsonResult<PageJsonResultVo<T>>();
        result.setSuccess(true);
        result.setCode(JsonResultEnum.SUCCESS.getCode());
        result.setMessage(JsonResultEnum.SUCCESS.getText());
        PageJsonResultVo<T> pageJsonResultVo = new PageJsonResultVo<>();
        pageJsonResultVo.setPage(current)
                        .setPageSize(size)
                        .setPages(pages)
                        .setTotal(total)
                        .setItems(records);
        result.setData(pageJsonResultVo);
        return result;
    }

    public static <T> JsonResult<PageJsonResultVo<T>> buildSuccessResult(PageJsonResultVo<T> pageJsonResultVo) {
        JsonResult<PageJsonResultVo<T>> result = new JsonResult<PageJsonResultVo<T>>();
        result.setSuccess(true);
        result.setCode(JsonResultEnum.SUCCESS.getCode());
        result.setMessage(JsonResultEnum.SUCCESS.getText());
        result.setData(pageJsonResultVo);
        return result;
    }


    protected static <T> JsonResult<T>  buildResult(String code, String message, boolean success) {
        JsonResult<T> jsonResult = new JsonResult<T>();
        jsonResult.setCode(code);
        jsonResult.setMessage(message);
        jsonResult.setSuccess(success);
        jsonResult.setData(null);
        return jsonResult;
    }


    public static <T> JsonResult<T>  buildSuccessResult(String message) {
        return buildResult(JsonResultEnum.SUCCESS.getCode(), message, true);
    }

    public static <T> JsonResult<T>  buildFailedResult(String message) {
        return buildResult(JsonResultEnum.FAIL.getCode(), message, false);
    }

    public static <T> JsonResult<T> buildFailedResult(String code, String message) {
        return buildResult(code, message, false);
    }


}
