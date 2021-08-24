package com.ck.common.mini.core.condition;

import com.ck.common.mini.core.DictTree;

/**
 * @Author caikun
 * @Description 条件解析器
 * 用于搜索传入参数后对返回的结果集进行筛选
 * @Date 下午5:41 21-8-23
 * @see DictTree
 **/
public interface ConditionMatcher<CONDITION, TARGET> {

    /**
     * 自行实现筛选方案
     *
     * @param condition 筛选数据来源
     * @param target    被判断对象
     * @return false 说明不匹配
     * true 说明匹配，可以返回
     */
    boolean match(CONDITION condition, TARGET target);
}
