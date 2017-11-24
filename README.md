## Prophet是什么？ 
一个优秀的hive查询平台，提供异步任务查询、用户、数据权限控制、历史查询任务与结果、hive建表、定时任务等功能。

## 开发环境：
* java 8
* springboot
* iview

## 安装步骤：
* 1.安装java
    * 安装jdk
    * 修改PATH
* 2.安装prophet后端
    * 修改配置文件
    * 启动服务

## 性能调优：
* prophet JVM能容纳的最大并发线程数NThreads = CPU核心数 * 总CPU利用率 * (1 + CPU等待时间/CPU处理时间)
    * 如果一个任务CPU处理时间为100ms，99ms是IO等待时间，系统8核心，CPU利用率50%，则NThreads = 8 * 50% * (1 + 99/100) = 7.96 ~ 8
    * 该指标可用于估算单进程prophet最大可运行的并发任务数
    * 如果指标不够则需要扩容
