package com.lavender.discovery.impl;

import com.lavender.Constant;
import com.lavender.ErpcBootStrap;
import com.lavender.utils.NetUtils;
import com.lavender.ServiceConfig;
import com.lavender.discovery.AbstractRegistry;
import com.lavender.exceptions.DiscoverRegistryException;
import com.lavender.utils.ZooKeeperNode;
import com.lavender.utils.ZooKeeperUtil;
import com.lavender.watch.OnLineAndOffLineWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 18:27
 **/
@Slf4j
public class ZooKeeperRegistry extends AbstractRegistry {
    private ZooKeeper zooKeeper;

    public ZooKeeperRegistry(){
        this.zooKeeper = ZooKeeperUtil.createZooKeeper();
    }
    public ZooKeeperRegistry(String connectString, int timeout){
        this.zooKeeper = ZooKeeperUtil.createZooKeeper(connectString, timeout);
    }
    @Override
    public void register(ServiceConfig<?> service) {
        String parentNode = Constant.BASE_PROVIDERS_PATH+"/"+service.getInterface().getName();
        if(!ZooKeeperUtil.exists(zooKeeper, parentNode, null)){
            ZooKeeperNode zooKeeperNode = new ZooKeeperNode(parentNode, null);
            ZooKeeperUtil.createNode(zooKeeper, zooKeeperNode, null, CreateMode.PERSISTENT);
        }


        String groupNode = parentNode + "/"+service.getGroup();
        if(!ZooKeeperUtil.exists(zooKeeper, groupNode, null)){
            ZooKeeperNode zooKeeperNode = new ZooKeeperNode(groupNode, null);
            ZooKeeperUtil.createNode(zooKeeper, zooKeeperNode, null, CreateMode.PERSISTENT);
        }

        String node = groupNode + "/" + NetUtils.getIp() + ":" + ErpcBootStrap.getInstance().getConfiguration().getPort();
        if(!ZooKeeperUtil.exists(zooKeeper, node, null)){
            ZooKeeperNode zooKeeperNode = new ZooKeeperNode(node, null);
            ZooKeeperUtil.createNode(zooKeeper, zooKeeperNode, null, CreateMode.EPHEMERAL);
        }

        if(log.isDebugEnabled()){
            log.debug("服务{}, 已经被注册", service.getInterface().getName());
        }

    }

    @Override
    public List<InetSocketAddress> lookup(String serviceName, String group) {
        // 1, find the node that provide service
        String serviceNode = Constant.BASE_PROVIDERS_PATH + "/" + serviceName + "/" + group;

        // 2, get child of the node
        List<String> children =  ZooKeeperUtil.getChildren(zooKeeper, serviceNode, new OnLineAndOffLineWatcher());
        List<InetSocketAddress> inetSocketAddresses = children.stream().map(ipString -> {
            String[] ipAndPort = ipString.split(":");
            String ip = ipAndPort[0];
            int port = Integer.parseInt(ipAndPort[1]);
            return new InetSocketAddress(ip, port);
        }).toList();

        if(inetSocketAddresses.isEmpty()){
            throw new DiscoverRegistryException("未发现任何可用的主机。");
        }
        return inetSocketAddresses;
    }
}
