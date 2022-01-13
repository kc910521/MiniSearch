package com.ck.common.mini.util;

import com.ck.common.mini.cluster.Intent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashMap;

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

    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static String toJsonString(Object o) {
        String s = null;
        try {
            s = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static Intent parseIntentFrom(byte[] json) {
        Intent res = null;
        try {
            res = mapper.readValue(json, Intent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Intent parseIntentFrom(String json) {
        Intent res = null;
        try {
            res = mapper.readValue(json, Intent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void main(String[] args) {
//        Intent intent = new Intent(System.currentTimeMillis());
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("sas", "蔡焜");
//        intent.setAction("ADD");
//        intent.setCarrier(map);
//        intent.setVersion(1642062159799L);
//        String s = toJsonString(intent);
//        System.out.println(s);


        String a2 = "\"{\\n  \\\"indexName\\\" : \\\"aidx\\\",\\n  \\\"carrier\\\" : \\\"aasaok\\\",\\n  \\\"version\\\" : 1642062633593,\\n  \\\"action\\\" : \\\"ADD\\\",\\n  \\\"key\\\" : \\\"aasaok\\\"\\n}\"";

        Intent o = parseIntentFrom(a2);
        System.out.println(o.getCarrier());

    }



    private SpringTools() {
    }

}
