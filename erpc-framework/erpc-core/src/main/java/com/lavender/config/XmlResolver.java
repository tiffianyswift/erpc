package com.lavender.config;

import com.lavender.IDGenerator;
import com.lavender.compress.Compressor;
import com.lavender.compress.CompressorFactory;
import com.lavender.discovery.RegistryConfig;
import com.lavender.loadbalancer.LoadBalancer;
import com.lavender.serialiize.Serializer;
import com.lavender.serialiize.SerializerFactory;
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
import java.util.Objects;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-05 09:04
 **/
@Slf4j
public class XmlResolver {
    public void loadFromXml(Configuration configuration) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("erpc.xml");
            Document doc = builder.parse(inputStream);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            configuration.setPort(resolvePort(doc, xpath));
            configuration.setApplicationName(resolveApplicationName(doc, xpath));
            configuration.setRegistryConfig(resolveRegistryConfig(doc, xpath));

            configuration.setCompressType(resolveCompressType(doc, xpath));
            CompressorFactory.addCompresssor(resolveCompressor(doc, xpath));

            configuration.setSerializeType(resolveSerializeType(doc, xpath));
            SerializerFactory.addSerializer(resolveSerializer(doc, xpath));

            configuration.setLoadBalancer(resolveLoadBalancer(doc, xpath));
            configuration.setIdGenerator(resolveIdGenerator(doc, xpath));




        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.info("加载配置发生错误，使用默认配置。");
        }
    }


    private int resolvePort(Document doc, XPath xpath) {
        String expression = "/configuration/port";
        return Integer.parseInt(parseString(doc, xpath, expression));
    }

    private String resolveApplicationName(Document doc, XPath xpath) {
        String expression = "/configuration/appName";
        return parseString(doc, xpath, expression);
    }
    private RegistryConfig resolveRegistryConfig(Document doc, XPath xpath) {
        String expression = "/configuration/registry";
        String url = parseString(doc, xpath, expression, "url");
        return new RegistryConfig(url);
    }
    private String resolveSerializeType(Document doc, XPath xpath) {
        String expression = "/configuration/serializeType";
        return parseString(doc, xpath, expression, "type");
    }
    private ObjectWrapper<Serializer> resolveSerializer(Document doc, XPath xpath) {
        String expression = "/configuration/serializer";
        Serializer serializer = parseObject(doc, xpath, expression, null);
        Byte code = Byte.valueOf(Objects.requireNonNull(parseString(doc, xpath, expression,"code")));
        String name = parseString(doc, xpath, expression, "name");
        return new ObjectWrapper<>(code, name, serializer);
    }


    private String resolveCompressType(Document doc, XPath xpath) {
        String expression = "/configuration/compressType";
        return parseString(doc, xpath, expression, "type");
    }
    private ObjectWrapper<Compressor> resolveCompressor(Document doc, XPath xpath) {
        String expression = "/configuration/compressor";
        Compressor compressor = parseObject(doc, xpath, expression, null);
        Byte code = Byte.valueOf(Objects.requireNonNull(parseString(doc, xpath, expression,"code")));
        String name = parseString(doc, xpath, expression, "name");
        return new ObjectWrapper<>(code, name, compressor);

    }

    private LoadBalancer resolveLoadBalancer(Document doc, XPath xpath) {
        String expression = "/configuration/loadBalancer";
        return parseObject(doc, xpath, expression, null);
    }







    private IDGenerator resolveIdGenerator(Document doc, XPath xpath) {
        String expression = "/configuration/idGenerator";
        String clazzName = parseString(doc, xpath, expression, "class");
        String dataCenterId = parseString(doc, xpath, expression, "dataCenterId");
        String machineId = parseString(doc, xpath, expression, "machineId");

        try {
            Class<?> clazz = Class.forName(clazzName);
            Object instance = clazz.getConstructor(new Class[]{long.class, long.class}).newInstance(Long.parseLong(dataCenterId), Long.parseLong(machineId));
            return (IDGenerator) instance;
        } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    private <T> T parseObject(Document doc, XPath xpath, String expression, Class<?>[] paramType, Object... param) {
        try {
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            String className = node.getAttributes().getNamedItem("class").getNodeValue();
            Class<?> clazz = Class.forName(className);
            Object instance = null;
            if(paramType == null){
                instance = clazz.getConstructor().newInstance();
            }
            else{
                instance = clazz.getConstructor(paramType).newInstance(param);
            }
            return (T)instance;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | XPathExpressionException e) {
            log.error("解析表达式时发生异常。");
        }
        return null;
    }
    private String parseString(Document doc, XPath xpath, String expression, String attributeName){
        try {
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            return node.getAttributes().getNamedItem(attributeName).getNodeValue();
        } catch (XPathExpressionException e) {
            log.error("解析表达式时发生异常。");
        }
        return null;
    }
    private String parseString(Document doc, XPath xpath, String expression){
        try {
            XPathExpression expr = xpath.compile(expression);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            return node.getTextContent();
        } catch (XPathExpressionException e) {
            log.error("解析表达式时发生异常。");
        }
        return null;
    }
}
