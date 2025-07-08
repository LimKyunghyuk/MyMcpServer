#!/bin/bash
# Claude Desktop과 MCP 서버 연동을 위한 설정 스크립트

echo "=== Claude Desktop MCP 서버 연동 설정 ==="

# 현재 프로젝트 경로 가져오기
PROJECT_PATH=$(pwd)
JAR_PATH="$PROJECT_PATH/target/mcp-server-example-1.0.0.jar"
SCRIPT_PATH="$PROJECT_PATH/scripts/run-server.sh"

echo "프로젝트 경로: $PROJECT_PATH"

# 1. JAR 파일 빌드
echo
echo "1. JAR 파일 빌드 중..."
mvn clean package -DskipTests

if [ ! -f "$JAR_PATH" ]; then
    echo "❌ JAR 파일 빌드에 실패했습니다."
    exit 1
fi

echo "✅ JAR 파일이 성공적으로 빌드되었습니다."

# 2. 스크립트 실행 권한 부여
echo
echo "2. 스크립트 실행 권한 설정 중..."
chmod +x scripts/run-server.sh
echo "✅ 스크립트 실행 권한이 설정되었습니다."

# 3. 운영체제별 Claude Desktop 설정 파일 경로 확인
echo
echo "3. Claude Desktop 설정 파일 경로:"

if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    CLAUDE_CONFIG_PATH="$HOME/Library/Application Support/Claude/claude_desktop_config.json"
    echo "macOS 감지됨"
    echo "설정 파일 경로: $CLAUDE_CONFIG_PATH"
    
    CONFIG_CONTENT="{
  \"mcpServers\": {
    \"java-mcp-server\": {
      \"command\": \"/bin/bash\",
      \"args\": [
        \"$SCRIPT_PATH\"
      ]
    }
  }
}"

elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    CLAUDE_CONFIG_PATH="$HOME/.config/Claude/claude_desktop_config.json"
    echo "Linux 감지됨"
    echo "설정 파일 경로: $CLAUDE_CONFIG_PATH"
    
    CONFIG_CONTENT="{
  \"mcpServers\": {
    \"java-mcp-server\": {
      \"command\": \"/bin/bash\",
      \"args\": [
        \"$SCRIPT_PATH\"
      ]
    }
  }
}"

else
    echo "지원되지 않는 운영체제입니다. Windows의 경우 setup-claude-integration.bat을 사용하세요."
    exit 1
fi

# 4. 설정 파일 디렉토리 생성
echo
echo "4. Claude Desktop 설정 디렉토리 생성 중..."
mkdir -p "$(dirname "$CLAUDE_CONFIG_PATH")"

# 5. 설정 파일 생성 (기존 파일이 있으면 백업)
echo
echo "5. Claude Desktop 설정 파일 생성 중..."

if [ -f "$CLAUDE_CONFIG_PATH" ]; then
    echo "⚠️  기존 설정 파일이 발견되었습니다."
    echo "백업을 생성합니다: ${CLAUDE_CONFIG_PATH}.backup"
    cp "$CLAUDE_CONFIG_PATH" "${CLAUDE_CONFIG_PATH}.backup"
fi

echo "$CONFIG_CONTENT" > "$CLAUDE_CONFIG_PATH"
echo "✅ Claude Desktop 설정 파일이 생성되었습니다."

# 6. 설정 파일 내용 표시
echo
echo "6. 생성된 설정 파일 내용:"
echo "----------------------------------------"
cat "$CLAUDE_CONFIG_PATH"
echo "----------------------------------------"

# 7. 최종 안내
echo
echo "=== 설정 완료! ==="
echo
echo "다음 단계를 수행하세요:"
echo "1. Claude Desktop을 완전히 종료하세요"
echo "2. Claude Desktop을 다시 시작하세요"
echo "3. 새로운 대화에서 다음과 같이 테스트하세요:"
echo "   '현재 시간을 알려주세요'"
echo "   '10 + 5를 계산해주세요'"
echo "   '안녕하세요라고 인사해주세요'"
echo
echo "문제가 발생하면 다음 로그를 확인하세요:"
echo "- 서버 로그: $PROJECT_PATH/logs/mcp-server.log"
echo "- Claude Desktop 로그: ~/Library/Logs/Claude (macOS) 또는 ~/.local/share/Claude/logs (Linux)"
echo
echo "🎉 연동 설정이 완료되었습니다!"