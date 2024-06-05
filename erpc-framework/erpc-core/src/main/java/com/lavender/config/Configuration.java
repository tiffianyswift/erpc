package com.lavender.config;

import com.lavender.IDGenerator;
import com.lavender.ProtocolConfig;
import com.lavender.compress.Compressor;
import com.lavender.compress.impl.GzipCompressor;
import com.lavender.discovery.RegistryConfig;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.loadbalancer.impl.RoundRobinLoadBalancer;
import com.lavender.serialiize.Serializer;
import com.lavender.serialiize.impl.JdkSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-04 09:56
 **/
@Data
@Slf4j
public class Configuration {
    private int port = 8098;
    private String applicationName = "default";
    private RegistryConfig registryConfig = new RegistryConfig("zookeeper://49.235.128.207:2181");

    private String serializeType = "jdk";

    private String compressType = "gzip";

    private IDGenerator idGenerator = new IDGenerator(1, 1);



    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    public Configuration(){
        // 1, 成员变量
        SpiResolver spiResolver = new SpiResolver();
        spiResolver.loadFromSpi(this);

        XmlResolver xmlResolver = new XmlResolver();
        xmlResolver.loadFromXml(this);

    }




    public static void main(String[] args) {
        Configuration configuration = new Configuration();
    }


}
