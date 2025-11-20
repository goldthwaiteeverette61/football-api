#!/bin/bash

# --- 配置区 ---
# JAR包的名称
APP_NAME="ruoyi-snailjob-server.jar"
# JVM启动参数 (可根据服务器配置调整)
JVM_OPTS="-Xms512m -Xmx1024m"
# Spring Boot运行环境
SPRING_PROFILES="--spring.profiles.active=prod"
# 日志文件名称
LOG_FILE="uoyi-snailjob-server.log"

# --- 脚本核心逻辑 ---

# 1. 查找正在运行的进程PID
# 使用ps和grep命令查找，并用grep -v grep排除grep自身的进程
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')

# 2. 如果找到了PID，就停止该进程
if [ -n "$PID" ]; then
    echo "发现正在运行的 $APP_NAME 进程 (PID: $PID), 正在停止..."
    kill $PID
    # 等待几秒钟，确保进程已完全停止
    sleep 3
    # 再次检查进程是否已停止
    if ps -p $PID > /dev/null; then
        echo "无法正常停止进程，尝试强制停止 (kill -9)..."
        kill -9 $PID
        sleep 1
    fi
    echo "进程已停止。"
else
    echo "没有发现正在运行的 $APP_NAME 进程。"
fi

# 3. 启动新的进程
echo "正在启动 $APP_NAME ..."
nohup java $JVM_OPTS -jar $APP_NAME $SPRING_PROFILES > $LOG_FILE 2>&1 &

# 4. 验证启动结果
# 延时几秒等待应用启动
sleep 5
NEW_PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -n "$NEW_PID" ]; then
    echo "$APP_NAME 启动成功! (PID: $NEW_PID)"
    echo "您可以使用 'tail -f $LOG_FILE' 命令查看实时日志。"
else
    echo "$APP_NAME 启动失败，请检查日志文件 '$LOG_FILE' 获取详细信息。"
fi
