package com.cg.docService.elasticsearch;

import java.util.function.Consumer;

/**
 * Author: MIZUGI
 * Date: 2025/11/21
 * Description:
 */
public class Utils {
    public static String moveStr(String json){
        int index = json.indexOf(": ");
        if (index != -1) {
            return json.substring(index + 2);
        }
        return json;
    }
    static <T> T applyIf(boolean condition, T target, Consumer<T> action) {
        if (condition) {
            action.accept(target);
        }
        return target;
    }
}