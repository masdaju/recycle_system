package com.cg.utils;

public class StringUtils {

    /**
     * 获取文件名
     * @param preAvatar
     * @return
     */
    public static String topath(String preAvatar) {
        int lastSlashIndex = preAvatar.lastIndexOf('/');

        // 获取文件名
        return preAvatar.substring(lastSlashIndex + 1);
    }
}
