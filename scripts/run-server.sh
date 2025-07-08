#!/bin/bash
# macOS/Linux용 MCP 서버 실행 스크립트

# 스크립트가 있는 디렉토리의 상위 디렉토리로 이동
cd "$(dirname "$0")/.."

# 로그 디렉토리 생성
mkdir -p logs

# Maven으로 JAR 파일 빌드 (없는 경우)
if [ ! -f "target/mcp-server-example-1.0.0.jar" ]; then
    mvn clean package -DskipTests >/dev/null 2>&1
fi

# 서버 실행 (UTF-8 인코딩 설정)
export LANG=ko_KR.UTF-8
export LC_ALL=ko_KR.UTF-8
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar target/mcp-server-example-1.0.0.jar