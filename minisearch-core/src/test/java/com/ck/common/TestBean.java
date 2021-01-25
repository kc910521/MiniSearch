package com.ck.common;

/**
 * @Author caikun
 * @Description //TODO $END
 * @Date 下午4:34 20-4-21
 **/
public class TestBean {

    private String name;

    private int id = 3;

    public TestBean(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name + "," + id;
    }
}
