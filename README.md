# Online Learning Coding Platform

此项目为前后端分离的在线编程学习平台的后端原型，基于 Spring Boot 3、Spring Security + JWT、Spring Data JPA 和 MySQL 构建。

## 技术栈
- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.x
- Maven
- Lombok

## 快速开始
1. 修改 `backend/src/main/resources/application.yml` 中的数据库连接配置。
2. 运行后端：
   ```bash
   mvn -f backend/pom.xml spring-boot:run
   ```

## 认证接口
- `POST /auth/register`：注册 STUDENT/TEACHER 账号。
- `POST /auth/login`：登录并返回 JWT 与用户信息。

服务启动后监听端口 `8080`，已开启 CORS 允许 `http://localhost:5173` 与 `http://localhost:5175` 访问。
