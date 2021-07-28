package com.ck.common.mini.util;

import org.springframework.util.ClassUtils;

/**
 * @Author caikun
 * @Description spring 相关工具
 * @Date 上午11:02 21-7-27
 **/
public final class SpringTools {

    /**
     * 判断是否存在class
     *
     * @param className
     * @return
     */
    public static boolean isClassPresent(String className) {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

        try {
            ClassUtils.forName(className, classLoader);
            return true;
        } catch (Throwable var6) {
            ClassLoader parent = classLoader.getParent();
            if (parent == null) {
                return false;
            } else {
                try {
                    ClassUtils.forName(className, parent);
                    return true;
                } catch (Throwable var5) {
                    return false;
                }
            }
        }
    }

    private SpringTools() {
    }

}
