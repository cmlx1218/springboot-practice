package com.cmlx.zookeeper.single;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @Desc 分布式配置中心demo
 * @Author cmlx
 * @Date 2020-5-14 0014 15:13
 */
public class ZookeeperProSync implements Watcher {

    private static CountDownLatch connectiedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        //zookeeper配置数据存放路径
        String path = "/username";
        //连接Zookeeper并且注册一个默认的监听器
        zk = new ZooKeeper("39.96.178.201:2181", 5000, new ZookeeperProSync());
        //等待zk连接成功的通知
        connectiedSemaphore.await();
        //获取path目录节点的配置数据，并注册默认的监听器
        System.out.println(new String(zk.getData(path, true, stat)));
        Thread.sleep(Integer.MAX_VALUE);
    }


    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) { //zk连接成功通知事件
                connectiedSemaphore.countDown();
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    System.out.println("配置已修改，新值为：" + new String(zk.getData(event.getPath(), true, stat)));
                } catch (Exception e) {

                }
            }
        }
    }
}
