## Prophet是什么？ 
一个优秀的大数据查询平台，提供hive异步任务查询、LDAP用户、数据权限控制、历史查询任务与结果存储、邮件通知、excel下载等功能。

## 开发环境：
* java 8
* springboot
* VUE + iview

## 安装步骤--非编译方式
* 1.安装java
    * 安装jdk
    * 修改PATH
* 2.安装prophet后端
    * 修改配置文件
    * 启动服务

## 安装步骤--编译方式
* 1. 编译prophet-fe项目，具体参照prophet-fe项目README.md
* 1. 编译prophet后端
    * eclipse里在prophet项目Run As -> Maven package或Maven build..后输入package

## 性能调优：
* prophet JVM能容纳的最大并发线程数NThreads = CPU核心数 * 总CPU利用率 * (1 + CPU等待时间/CPU处理时间)
    * 如果一个任务CPU处理时间为100ms，99ms是IO等待时间，系统8核心，CPU利用率50%，则NThreads = 8 * 50% * (1 + 99/100) = 7.96 ~ 8
    * 该指标可用于估算单进程prophet最大可运行的并发任务数
    * 如果指标不够则需要扩容

## docker镜像化
首先看Spring Boot应用程序的docker化，由于Spring Boot内嵌了tomcat、Jetty等容器，因此我们对docker镜像的要求就是需要java运行环境。我的应用代码的的Dockerfile文件如下：
#基础镜像：仓库是java，标签用8u66-jdk
FROM java:8u66-jdk
#当前镜像的维护者和联系方式
MAINTAINER duqi duqi@example.com
#将打包好的spring程序拷贝到容器中的指定位置
ADD target/bookpub-0.0.1-SNAPSHOT.jar /opt/bookpub-0.0.1-SNAPSHOT.jar
#容器对外暴露8080端口
EXPOSE 8080
#容器启动后需要执行的命令
CMD java -Djava.security.egd=file:/dev/./urandom -jar /opt/bookpub-0.0.1-SNAPSHOT.jar
因为目前的示例程序比较简单，这个dockerfile并没有在将应用程序的数据存放在宿主机上。如果你的应用程序需要写文件系统，例如日志，最好利用VOLUME /tmp命令，这个命令的效果是：在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/tmp目录。
把这个Dockerfile放在项目的根目录下即可，后续通过docker-compose build统一构建：基础镜像是只读的，然后会在该基础镜像上增加新的可写层来供我们使用，因此java镜像只需要下载一次。
docker-compose是用来做docker服务编排，参看《Docker从入门到实践》中的解释：
Compose 项目目前在 Github 上进行维护，目前最新版本是 1.2.0。Compose 定位是“defining and running complex applications with Docker”，前身是 Fig，兼容 Fig 的模板文件。
Dockerfile 可以让用户管理一个单独的应用容器；而 Compose 则允许用户在一个模板（YAML 格式）中定义一组相关联的应用容器（被称为一个 project，即项目），例如一个 Web 服务容器再加上后端的数据库服务容器等。
单个docker用起来确实没什么用，docker技术的关键在于持续交付，通过与jekins的结合，可以实现这样的效果：开发人员提交push，然后jekins就自动构建并测试刚提交的代码，这就是我理解的持续交付。

## 后端.json接口返回status值说明：
* 0: 正常
* 1: 后端有异常，详见message
* 2: 用户未登录
* 3: 查询的数据表里有机密数据，而且当前用户没有该表查询权限
