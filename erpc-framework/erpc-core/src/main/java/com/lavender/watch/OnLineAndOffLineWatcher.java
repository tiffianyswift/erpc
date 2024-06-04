package com.lavender.watch;

import com.lavender.ErpcBootStrap;
import com.lavender.discovery.NettyBootstrapInitializer;
import com.lavender.discovery.Registry;
import com.lavender.loadbalancer.LoadBalancer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-03 21:19
 **/
@Slf4j
public class OnLineAndOffLineWatcher implements Watcher {

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
            if(log.isDebugEnabled()){
                log.debug("检测到服务【{}】有节点上下线，将重新拉取服务列表", watchedEvent.getPath());

            }
            System.out.println(watchedEvent.getPath() + "-------------------检测到有节点上下线，将重新拉取服务列表-----------------"+watchedEvent.getPath());
            Registry registry = ErpcBootStrap.getInstance().getConfiguration().getRegistryConfig().getRegistry();

            String[] split = watchedEvent.getPath().split("/");
            String serviceName = split[split.length-1];
            List<InetSocketAddress> addressList = registry.lookup(serviceName);
            for(InetSocketAddress address : addressList){
                if(!ErpcBootStrap.CHANNEL_CACHE.containsKey(address)){
                    try {
                        Channel channel = NettyBootstrapInitializer.getBootstrap().connect(address).sync().channel();
                        ErpcBootStrap.CHANNEL_CACHE.put(address, channel);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            for(Map.Entry<InetSocketAddress, Channel> entry : ErpcBootStrap.CHANNEL_CACHE.entrySet()){
                if(!addressList.contains(entry.getKey())){
                    ErpcBootStrap.CHANNEL_CACHE.remove(entry.getKey());
                }
            }
            LoadBalancer loadBalancer = ErpcBootStrap.getInstance().getConfiguration().getLoadBalancer();
            loadBalancer.reLoadBalance(serviceName, addressList);
        }
    }
}
