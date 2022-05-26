package com.ck.common;

import com.ck.common.mini.config.MiniSearchConfigure;
import com.ck.common.mini.index.struct.MiniInstance;
import com.ck.common.mini.util.MiniSearch;
import com.ck.common.util.CheckUtil;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;

public class JobTest {

    @Test
    public void timingAdditionsJob() throws InterruptedException {
        // init instance by config 3sec addition
        MiniSearchConfigure configure = new MiniSearchConfigure();
        configure.setRebuildTaskInterval(3);
        MiniInstance instance = MiniSearch.findInstance("great_job0", configure);
        Collection<Object> results = instance.find("tomcat");
        CheckUtil.sizeCheck(0, results);

        MiniSearch.registerJob("great_job0", ms -> {
            long ts = System.currentTimeMillis();
            ms.add("tom cat" + ts, "wov!wov!" + ts);
        });

        for (int i = 0; i < 5; i ++) {
            Thread.sleep(5000);
            results = instance.find("tomcat");
            System.out.println(results);
        }
    }
}
