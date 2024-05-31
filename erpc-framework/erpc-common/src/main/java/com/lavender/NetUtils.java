package com.lavender;

import com.lavender.exceptions.NetworkException;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-05-30 17:25
 **/

@Slf4j
public class NetUtils {
    public static String getIp(){
        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()){
                NetworkInterface iface = interfaces.nextElement();
                if(iface.isLoopback() || iface.isVirtual() || !iface.isUp()){
                    continue;
                }
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()){
                    InetAddress addr = addresses.nextElement();
                    if(addr instanceof Inet6Address || addr.isLoopbackAddress()){
                        continue;
                    }
                    String ipAddress = addr.getHostAddress();
                    System.out.println("局域网IP地址："+ipAddress);
                    return ipAddress;
                }
            }
            throw new NetworkException();
        }
        catch (SocketException ex){
            log.error("获取局域网IP时发生异常", ex);
            throw new NetworkException();

        }
    }

    public static void main(String[] args) {
        System.out.println(getIp());
    }
}
