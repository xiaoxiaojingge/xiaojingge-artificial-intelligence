package com.itjing.ai.bayes.util;

import java.util.List;

/**
 * List工具
 *
 * @author lijing
 * @date 2024-07-06
 */
public class ListUtil {

    /**
     * 判断列表中指定索引位置是否有元素
     *
     * @param list  要检查的列表
     * @param index 要检查的索引位置
     * @return 如果索引在列表范围内且有元素，则返回 true；否则返回 false
     */
    public static <T> boolean isElementPresent(List<T> list, int index) {
        return index >= 0 && index < list.size();
    }

    /**
     * 获取列表中指定索引位置的元素，如果索引有效
     *
     * @param list  要访问的列表
     * @param index 要访问的索引位置
     * @return 如果索引有效，则返回该索引位置的元素；否则返回 null
     */
    public static <T> T getElementIfPresent(List<T> list, int index) {
        if (isElementPresent(list, index)) {
            return list.get(index);
        }
        return null;
    }
}