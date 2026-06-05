# 达梦数据库驱动

本目录用于存放达梦数据库 JDBC 驱动。

## 下载驱动

1. 访问达梦官网：https://www.dameng.com/
2. 下载 DmJdbcDriver18-8.1.2.2.jar 或更新版本
3. 将 jar 文件放置到本目录

## 或者使用 Maven 安装

```bash
# 从达梦官网获取驱动后，执行：
mvn install:install-file \
    -Dfile=lib/DmJdbcDriver18-8.1.2.2.jar \
    -DgroupId=com.dameng \
    -DartifactId=DmJdbcDriver18 \
    -Dversion=8.1.2.2 \
    -Dpackaging=jar
```

## 驱动版本说明

| 驱动版本 | 数据库版本 | 说明 |
|----------|-----------|------|
| 8.1.2.2 | DM8 | 推荐版本 |
| 8.1.1.49 | DM8 | 兼容版本 |
