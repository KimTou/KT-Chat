# KT-Chat 分布式即时聊天系统
**技术选型**：Java、SpringCloud、Nacos、Sentinel、Netty、MySQL、Redis、RocketMQ 等

**项目描述**：项目基于 SpringCloud Gateway + Nacos + Sentinel + OpenFeign 作为分布式系统架构，基于 Netty 实现高性能网络通信。主要功能有：一对一聊天以及群组聊天、好友管理、群组管理等。

项目独立完成，包括需求分析、设计、开发实现。

关于我在项目中使用 MySQL 读写分离的总结：[MySQL主从延迟的解决方案](https://blog.csdn.net/KIMTOU/article/details/125033199)

## 用例分析

用户能够在聊天系统上进行网络通信，与好友进行实时一对一聊天，与群组成员进行群聊。用户用例图如下所示：

1. 用户登录：登录系统
2. 聊天：包含与好友进行一对一聊天，与群组成员进行群聊。
3. 群聊管理：新建群聊、加入群聊、退出群聊。
4. 好友管理：显示好友列表，添加、删除好友。
5. 在线离线状态显示：查看好友的在线、离线状态。
6. 聊天记录管理：将聊天记录存入数据库，能够显示、删除存储的聊天记录。

<img src="https://cdn.tojintao.cn/Chat原理图.png" alt="img" style="zoom:67%;" />

## 系统设计

#### 系统总体设计

分布式即时聊天系统分为用户信息子系统、长连接管理子系统、聊天信息子系统共三个子系统，API 网关负责将请求路由至各个子系统。

* 用户信息子系统包含权限校验模块、用户登录模块、好友管理模块，其中好友管理模块包括好友列表、添加好友、删除好友功能；
* 长连接管理子系统包含在线状态管理模块、聊天主模块、消息推送模块，其中聊天主模块包括一对一聊天和群聊功能；
* 聊天信息子系统包含群聊管理模块、聊天记录管理模块，其中群聊管理模块包括新建群聊、加入群聊、退出群聊功能。

<img src="https://cdn.tojintao.cn/KT-Chat系统结构图.png" style="zoom:67%;" />

#### 系统架构设计

![](https://cdn.tojintao.cn/KT-Chat系统架构设计.png)

项目基于 Nacos 作为注册中心，将各个服务注册进 Nacos，包括 Netty 服务端；使用 SpringCloud Gateway 作为服务网关，是所有请求的统一入口；限流组件使用 Sentinel；基于 Netty 进行通信、维护长连接；RocketMQ 作为消息队列，处理聊天消息的异步入库以及解决分布式 Netty 节点问题； Zookeeper 用于分布式 id 的生成；Redis 用于记录用户在线状态以及记录 Netty 节点的元数据；MySQL 对数据进行持久化。

## 运行截图

#### 一对一聊天

![](https://cdn.tojintao.cn/聊天测试1.PNG)

#### 群聊

![](https://cdn.tojintao.cn/群聊测试1.PNG)
