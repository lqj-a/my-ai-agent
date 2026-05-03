# 用带 Node.js 的镜像作为基础，自带 npx 命令
FROM node:20-alpine

# 安装 JDK21（适配你的 SpringBoot 项目）
RUN apk add --no-cache openjdk21-jre

# 设置工作目录
WORKDIR /app

# 复制你本地打包好的jar（确保target目录里有这个文件）
COPY target/my-ai-agent-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8123

# 启动命令
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]