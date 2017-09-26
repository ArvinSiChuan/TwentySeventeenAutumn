# 负载均衡笔记 #
###### [- By Arvin (arvinsc@foxmail.com)](https://github.com/ArvinSiChuan)  ######
## 1.服务器规划 ##
计划采用三台主机，其中一台包括负载均衡器（Ngnix），拓扑结构大致如下：  
```
Server1(Ngnix,Tomcat1,Redis Server,Ubuntu 16.04,192.168.1.123/24)  
↓        
↓  
Server2
(Window10,Tomcat2 v9,192.168.1.102/24)   

&  

Server1(Ngnix,Tomcat1,Redis Server,Ubuntu 16.04)  
↓        
↓  
Server3
(Ubuntu 16.04,Tomcat3,192.168.1.119)
```

## 2.服务器软件版本以及权重 ##
| Server | Server Software     |Weight|  
| :------------- | :------------- |----|
|Server1|Ngnix + Tomcat 8.5.20|1|
|Server2|Tomcat 8.5.20|2|
|Server3|Tomcat 8.5.20|2|

## 3.Nginx 安装配置 ##
使用包管理器安装，安装之前需要注意卸载Apache2（若有安装）。
安装完成后，对Nginx进行配置，配置文件如下：

```
user www-data;
worker_processes auto;
pid /run/nginx.pid;

events {
	worker_connections 768;
	# multi_accept on;
}

http {

	##
	# Basic Settings
	##

	sendfile on;
	tcp_nopush on;
	tcp_nodelay on;
	keepalive_timeout 65;
	types_hash_max_size 2048;
	# server_tokens off;

	# server_names_hash_bucket_size 64;
	# server_name_in_redirect off;

	include /etc/nginx/mime.types;
	default_type application/octet-stream;

	##
	# SSL Settings
	##

	ssl_protocols TLSv1 TLSv1.1 TLSv1.2; # Dropping SSLv3, ref: POODLE
	ssl_prefer_server_ciphers on;

	##
	# Logging Settings
	##

	access_log /var/log/nginx/access.log;
	error_log /var/log/nginx/error.log;

	##
	# Gzip Settings
	##

	gzip on;
	gzip_disable "msie6";

	# gzip_vary on;
	# gzip_proxied any;
	# gzip_comp_level 6;
	# gzip_buffers 16 8k;
	# gzip_http_version 1.1;
	gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

	##
	# Virtual Host Configs
	##

	include /etc/nginx/conf.d/*.conf;
	include /etc/nginx/sites-enabled/*;

        upstream 192.168.1.123{
                server 127.0.0.1:8080 weight=1;
                server 192.168.1.119:8080 weight=2;
                server 192.168.1.102:8080 weight=2;
        }

        server{
                listen 80;
                server_name 192.168.1.123;


		location / {
		        proxy_pass http://192.168.1.123;
             		proxy_redirect default;
       		}
	}

}


```
[注] 如果开启IP_HASH，对于同一个来访者（IP），则一定时间范围内只会指向同一个真实服务器，即等效于session锁定在一个服务器上。
## 4.调优 ##
Nginx默认情况下，对所有代理的节点，可以通过调整部分参数，使得在包含所有节点的整个集群环境，能够拥有良好的稳定性和扩展性。这些参数主要包括：代理超时重试次数、代理超时时间、代理超时重试的间隔时间和代理重定向设置。  
以下的配置，将代理超时重试次数（单次）设置为1，将其重试间隔时长设置为30s，连接超时时长设置为2s。如此设置，可以将单次单节点的等待时间减小到3s以内，在整个集群环境中，如果出现节点无法正常工作，则能够相对比较快地切换；在重新尝试异常节点上，有30s的时间间隔，使得对于问题节点不会过快重连，给予节点一定的应对时间，提高用户访问效率。
```
#configuration segments:

upstream 172.25.74.12{
	 #ip_hash;
	 server 127.0.0.1:8080 weight=1 max_fails=1 fail_timeout=60;
							 server 192.168.1.119:8080 weight=2 max_fails=1 fail_timeout=30;
							 server 172.25.67.43:8080 weight=2 max_fails=1 fail_timeout=30;
			 }

			 server{
							 listen 80;
							 server_name 172.25.74.12;



	 location / {
					 proxy_pass http://172.25.74.12;
							 proxy_redirect default;
		 proxy_connect_timeout 2s;
				 }
 }

```

## 5.Session共享 ##
对于Nginx+Tomcat的Session处理，有多种方式，主要有一下几类：
1. 单节点锁定，即，在用户访问时指定访问某一个节点，不进行访问中的动态切换，采用这种办法，只需要在Nginx配置中添加ip_hash选项即可；
2. 基于memcahed的session共享实现；
3. 基于Spring session的session共享，Spring session对于http session进行了处理，将session信息保存到某一个特定的数据源中。Spring session也同时对RESTful API、Websocket、Spring security、Spring Boot等都提供了良好的支持以及详细的配置说明文档。

为了便于以后的开发工作，尝试使用Spring Session进行Session共享，其中数据源支持由官方推荐的Redis提供。

首先添加依赖项，完整依赖项参见[GITHUB](https://github.com/ArvinSiChuan/TwentySeventeenAutumn/blob/springSession/pom.xml)：
```
<dependency>
		<groupId>org.springframework.security</groupId>
		<artifactId>spring-security-web</artifactId>
		<version>4.2.2.RELEASE</version>
</dependency>
<dependency>
		<groupId>org.springframework.session</groupId>
		<artifactId>spring-session-data-redis</artifactId>
		<version>1.3.1.RELEASE</version>
</dependency>
<!--redis-client-->
<dependency>
	 <groupId>biz.paluch.redis</groupId>
	 <artifactId>lettuce</artifactId>
	 <version>3.5.0.Final</version>
</dependency>
```
配置Spring session以及Redis连接：
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-4.3.xsd">

    <context:annotation-config />

    <!--load configuration file-->
    <context:property-placeholder location="classpath:redis.properties"/>

    <!--session necessary-->
    <bean id="httpSessionConfiguration" class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
        <property name="maxInactiveIntervalInSeconds" value="3600"/>
    </bean>
    <bean id="lettuceConnectionFactory" class="org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory">
        <property name="hostName" value="${redis_host}"/>
        <property name="port" value="${redis_port}"/>
        <property name="password" value="${redis_pass}"/>
        <property name="timeout" value="${redis_timeout}"/>
    </bean>

</beans>
```
```
#redis connection configuration

redis_host=192.168.1.111
redis_port=6379
redis_pass=[password]
redis_timeout=2000
```
在web.xml（[full_file_here](https://github.com/ArvinSiChuan/TwentySeventeenAutumn/blob/springSession/web/WEB-INF/web.xml)）中添加：
```
<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:springSession.xml</param-value>
</context-param>
<filter>
    <filter-name>springSessionRepositoryFilter</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:springSession.xml</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>springSessionRepositoryFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>ERROR</dispatcher>
</filter-mapping>
```
编写JSP以及Controller以体现session共享的实现效果：
```
<!-- Insert following lines in to-display jsp -->
<p>Your session id: ${pageContext.session.id},${Name}</p>
```
```
// Insert following lines in Controller Class
@RequestMapping(value = "/", method = RequestMethod.GET)
	public String helloIdea(Model model) {
			model.addAttribute("Name", "Value");
			return "hello_idea";
	}
```
编译并部署到各个tomcat服务器，可以看到，在被分发到不同节点上时，有相同的Sessionid，查看Redis数据库，也能够看到有以`Spring:session:`为前缀的Key生成，Session共享完成。




------
[参考资料]：
- [Spring session](http://projects.spring.io/spring-session/)(http://projects.spring.io/spring-session/)
- [tomcat+nginx+redis实现均衡负载、session共享](http://www.cnblogs.com/zhrxidian/p/5432886.html)(http://www.cnblogs.com/zhrxidian/p/5432886.html)
- [Redis 教程](http://www.runoob.com/redis/redis-tutorial.html)
- [Redis Official website](https://redis.io/)

[实验完整项目位置]
[git@github.com:ArvinSiChuan/TwentySeventeenAutumn.git - Branch:springSession](https://github.com/ArvinSiChuan/TwentySeventeenAutumn/tree/springSession)
