## Zookeeper
&emsp;分布式服务框架，主要用来解决分布式应用中遇到的一些数据管理问题  
&emsp;&emsp;eg：统一命名、状态同步、集群管理、分布式应用配置项的管理。
&emsp;&emsp;Zookeeper = 文件系统+监听通知机制  

---

#### 1、文件系统
&emsp;Zookeeper维护一个类似文件系统的数据结构：
![image](https://github.com/cmlx1218/springboot-practice/blob/master/zookeeper-practice/src/main/resources/images/zookeeper-01.png)
&emsp;每个子目录都被称作znode（目录节点），和文件系统一样，我们可以自由的增删znode，在一个znode下增删子znode，可以存储数据。
* PERSISTENT-持久化目录节点：客户端与Zookeeper断开连接后，该节点依旧存在
* PERSISTENT_SEQUENTIAL-持久化顺序编号目录节点：断开连接后，该节点依旧存在，只是Zookeeper给该节点名称进行顺序编号
* EPHEMERAL-临时目录节点：断开连接后，该节点被删除，只是Zookeeper给该节点名称进行顺序编号

---

####2、监听通知机制
客户端注册监听它关心的目录节点，当目录发生变化时（数据改变、被删除、子目录节点增加删除）时，Zookeeper会通知客户端

---

####3、Zookeeper单机模式安装
1、配置JAVA环境  
2、下载解压Zookeeper  
`wget http://mirrors.hust.edu.cn/apache/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz`  
`tar -zxvf zookeeper-3.4.13.tar.gz`  
`rm -f zookeeper-3.4.13.tar.gz`  
`cd /usr/local/zookeeper-3.4.13/conf`  
`cp zoo_sample.cfg zoo.cfg`  
`vim zoo.cfg`  
3、修改内容  
`dataDir=/usr/local/zookeeper-3.4.13/data    #这里最好自己设置`  
`server.1=cnblogs01:8888:9888         #这里修改为自己的主机名或者IP`  
`server.2=cnblogs02:8888:9888`    
`server.3=cnblogs03:8888:9888`  
4、创建/usr/local/zookeeper-3.4.13/data文件夹，新建一个myid,写入1  
`mkdir data`  
`vim myid`  
`1`  
5、启动  
`bin/zkServer.sh start`

