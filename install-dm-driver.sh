#!/bin/bash
# 达梦数据库驱动安装脚本

echo "=========================================="
echo "达梦数据库 JDBC 驱动安装"
echo "=========================================="

LIB_DIR="$(cd "$(dirname "$0")" && pwd)/lib"
DM_JAR="$LIB_DIR/DmJdbcDriver18-8.1.2.2.jar"

# 检查驱动文件
if [ -f "$DM_JAR" ]; then
    echo "发现达梦驱动: $DM_JAR"
    echo "正在安装到本地 Maven 仓库..."
    
    mvn install:install-file \
        -Dfile="$DM_JAR" \
        -DgroupId=com.dameng \
        -DartifactId=DmJdbcDriver18 \
        -Dversion=8.1.2.2 \
        -Dpackaging=jar \
        -DgeneratePom=true
    
    if [ $? -eq 0 ]; then
        echo "安装成功！"
    else
        echo "安装失败，请检查 Maven 配置"
        exit 1
    fi
else
    echo "错误: 未找到达梦驱动文件"
    echo "请从达梦官网下载 DmJdbcDriver18-8.1.2.2.jar 并放置到: $DM_JAR"
    exit 1
fi
