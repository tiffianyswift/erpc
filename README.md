# Lavender-eRPC

## 介绍

Lavender-eRPC 是一个基于 **Netty** 和 **Zookeeper** 的远程调用框架，支持与 **Spring Boot** 的无缝融合。该框架实现了服务调用方对服务提供方函数的远程调用，同时具备丰富的服务治理能力，如负载均衡、熔断限流以及优雅启停等功能。

### 主要特性

1. **多策略负载均衡**  
   支持调用端负载均衡，提供包括轮询、一致性哈希和最短响应时间等策略。
   
2. **服务稳定性**  
   具备心跳检测、异常重试和服务动态上下线功能，提升服务可靠性。
   
3. **多种序列化方式**  
   支持 JDK 原生序列化、Hessian 和 JSON 等序列化方式，满足不同场景需求。
   
4. **数据压缩支持**  
   支持 GZIP 的数据压缩方式，有效降低传输数据量，提高网络利用率。
   
5. **熔断与限流**  
   支持服务端熔断机制和基于 IP 的限流，确保服务的稳定性和安全性。
   
6. **流量隔离与优雅停机**  
   支持不同业务线的流量隔离，以及服务提供方的优雅停机机制，避免服务中断对调用方造成影响。
   
7. **高扩展性设计**  
   对负载均衡器、序列化器、压缩器和注册中心等核心组件进行了抽象设计，便于扩展和自定义。
   
8. **灵活的配置方式**  
   支持多种配置形式，包括方法配置、XML 配置和 SPI 配置，满足不同开发需求。

---

## 软件架构

以下是项目的基本架构图：

![项目架构图](https://github.com/user-attachments/assets/90a72ab2-76bf-4dad-a990-e1b5f37fd705)  

---

## 使用说明

### 调用方使用示例

以下展示了调用方的基本使用方式：

![调用方示例](https://github.com/user-attachments/assets/ce723a97-7783-4d50-8ffc-e2cdc1ce708e)  

### 服务方使用示例

以下展示了服务方的基本使用方式：

![服务方示例](https://github.com/user-attachments/assets/34e2b1ae-9ae6-4e00-9775-bf844acd98f2)  






