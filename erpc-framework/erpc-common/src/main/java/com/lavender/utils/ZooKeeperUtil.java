package com.lavender.utils;

import com.lavender.Constant;
import com.lavender.exceptions.ZooKeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 12:25
 **/
@Slf4j
public class ZooKeeperUtil {
    public static ZooKeeper createZooKeeper(){
        String connectString = Constant.DEFAULT_ZK_CONNECT;
        int timeout = Constant.TIME_OUT;
        return createZooKeeper(connectString, timeout);
    }
    public static ZooKeeper createZooKeeper(String connectString, int timeout){
        CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            final ZooKeeper zooKeeper = new ZooKeeper(connectString, timeout, event->{
                if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            return zooKeeper;

        } catch (IOException | InterruptedException e) {
            log.info("创建zookeeper时产生异常：", e);
            throw new ZooKeeperException();
        }

    }

    /**
     * create a zookeeper node
     * @param zooKeeper
     * @param node
     * @param watcher
     * @param createMode
     * @return
     */
    public static boolean createNode(ZooKeeper zooKeeper, ZooKeeperNode node, Watcher watcher, CreateMode createMode){
        try {
            if(zooKeeper.exists(node.getNodePath(), null) == null){
                String result = zooKeeper.create(node.getNodePath(), node.getData(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.info("节点【{}】，成功创建。", result);
                return true;
            }
            else{
                if(log.isDebugEnabled()){
                    log.debug("节点【{}】已经存在，无需创建。", node.getNodePath());
                }
                return false;
            }
        } catch (KeeperException | InterruptedException e) {
            log.info("创建节点时发生异常。", e);
            throw new ZooKeeperException();
        }
    }
    public static void close(ZooKeeper zooKeeper){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            log.error("关闭zookeeper时发生异常", e);
            throw new ZooKeeperException();
        }
    }

    public static boolean exists(ZooKeeper zooKeeper, String node, Watcher watcher){
        try {
            return zooKeeper.exists(node, watcher) != null;
        } catch (KeeperException | InterruptedException e) {
            log.error("判断节点【{}】是否发生异常", node, e);
            throw new ZooKeeperException(e);
        }
    }

    public static List<String> getChildren(ZooKeeper zooKeeper, String serviceNode, Watcher watcher) {
        try {
            return zooKeeper.getChildren(serviceNode, watcher);
        } catch (KeeperException | InterruptedException e) {
            log.error("获取节点【{}】的子元素时发生异常", serviceNode, e);
            throw new ZooKeeperException(e);
        }
    }
}
