package com.ck.common.mini.core.condition;

import com.ck.common.mini.util.LiteTools;

/**
 * @Author caikun
 * @Description 简单适配器
 * @Date 下午5:43 21-8-23
 **/
public class SimpleConditionMatcher<CONDITION, TARGET> implements ConditionMatcher<CONDITION, TARGET> {

    /**
     * 利用o的值去筛选o2是否相同
     *
     * @param o
     * @param o2
     * @return
     */
    @Override
    public boolean match(CONDITION o, TARGET o2) {
        return LiteTools.objectConditionMatch(o, o2);
    }
}
