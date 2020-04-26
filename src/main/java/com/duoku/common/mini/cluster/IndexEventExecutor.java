package com.duoku.common.mini.cluster;

/**
 * @Author caikun
 * @Description 索引事件发送器, 用户自发实现
 * @Date 下午1:52 20-4-24
 **/
public interface IndexEventExecutor {

    int init();

    <OBJ> int save(OBJ obj);

    <OBJ> int update(OBJ obj);

    <OBJ> int remove(OBJ obj);
}
