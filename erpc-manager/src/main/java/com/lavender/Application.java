package com.lavender;

import com.lavender.exceptions.ZooKeeperException;
import com.lavender.utils.ZooKeeperNode;
import com.lavender.utils.ZooKeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 10:59
 **/
@Slf4j
public class Application {
    public static void main(String[] args) {

        ZooKeeper zooKeeper = ZooKeeperUtil.createZooKeeper();
        String basePath = "/erpc-metadata";
        String providePath = basePath + "/providers";
        String consumerPath = basePath + "/consumers";

        ZooKeeperNode baseNode = new ZooKeeperNode(basePath, null);
        ZooKeeperNode providerNode = new ZooKeeperNode(providePath, null);
        ZooKeeperNode consumerNode = new ZooKeeperNode(consumerPath, null);

        List.of(baseNode, providerNode, consumerNode).forEach(node -> {
            ZooKeeperUtil.createNode(zooKeeper, node, null, CreateMode.PERSISTENT);
        });

//        ZooKeeperUtil.close(zooKeeper);


    }
}
