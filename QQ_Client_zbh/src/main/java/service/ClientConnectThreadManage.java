package service;

import java.util.HashMap;

public class ClientConnectThreadManage {
    // 4.0 线程管理更新
    // 将多个线程 放入 HashMap集合, key为用户id  value 为线程
    private static HashMap<String, HashMap<String, ClientConnectThread>> map = new HashMap<>();
    // 一个用户的线程集合    状态      对应线程
    private static HashMap<String, ClientConnectThread> stateMap = new HashMap<>();


    // 存放线程 进入集合的方法
    public static void addThread(String userId, String state, ClientConnectThread thread) {
        stateMap.put(state, thread);
        map.put(userId, stateMap);
    }


    // 根据userId 返回线程
    public static ClientConnectThread getThread(String userId, String state) {

        return map.get(userId).get(state);
    }

}
