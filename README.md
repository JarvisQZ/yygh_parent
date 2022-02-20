# 尚医通就医挂号平台

该项目是尚硅谷开源的基于Java SpringBoot 的微服务分布式全站项目

尚硅谷官网：http://www.atguigu.com/ <br>
B站视频：https://www.bilibili.com/video/BV1V5411K7rT

---

### 说明

该代码仓库是个人进行学习使用，对项目中一些细节进行了改进

### 项目介绍

尚医通项目是一款医院网上预约挂号系统，

### 一些改进

- 配置服务集群，如`Nacos`集群，`MySQL`主从，`redis-cluster`
- 完善`Nacos`作为配置中心的功能，将敏感的配置文件如 阿里云oss key，容联云短信服务api key 递交Nacos配置中心管理，计划将所有配置文件都由nacos管理
- 实现MySQL`读写分离`
- 对数据库进行优化，构建新的`索引`
- 对接口进行优化，避免 `select *`