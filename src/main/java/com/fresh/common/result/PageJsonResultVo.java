package com.fresh.common.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页数据的返回
 *  {
 *       "message": "SUCCESS",
 *       "code": "1",
 *       "success": true,
 *       "data": {
 *           "items": [
 *               {
 *               },
 *               {
 *               }
 *           ],
 *           "total": 1,
 *           "page": 1,
 *           "pageSize": 10,
 *           "pages": 1
 *       }
 *   }
 * @param <T> 数据的类型
 */
@Data
@Accessors(chain = true)
public class PageJsonResultVo<T> {
    private List<T> items;
    private long total; //总记录数
    private long page; //当前页
    private long pageSize; //每页多少条
    private long pages; //总页数
}
