# Claude Desktop과 MCP 서버 연동 가이드

이 가이드는 작성한 Java MCP 서버를 Claude Desktop 앱과 연동하는 방법을 설명합니다.

## 사전 준비

1. **Claude Desktop 설치**
   - [Claude Desktop 다운로드](https://claude.ai/download)
   - 운영체제에 맞는 버전 설치

2. **Java MCP 서버 빌드**
   ```bash
   mvn clean package
   ```

## 단계별 연동 방법

### 1단계: 설정 파일 위치 확인

운영체제별 Claude Desktop 설정 파일 위치:

- **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
- **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
- **Linux**: `~/.config/Claude/claude_desktop_config.json`

### 2단계: 설정 파일 생성/편집

#### 방법 1: 스크립트 사용 (권장)

**Windows:**
```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "cmd",
      "args": [
        "/c",
        "C:\\path\\to\\your\\project\\scripts\\run-server.bat"
      ]
    }
  }
}
```

**macOS/Linux:**
```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "/bin/bash",
      "args": [
        "/path/to/your/project/scripts/run-server.sh"
      ]
    }
  }
}
```

#### 방법 2: 직접 JAR 실행

```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/your/project/target/mcp-server-example-1.0.0.jar"
      ]
    }
  }
}
```

### 3단계: 절대 경로 설정

**중요**: 모든 경로는 절대 경로를 사용해야 합니다!

#### 현재 프로젝트 경로 확인:

**Windows (명령 프롬프트):**
```cmd
cd /d "프로젝트폴더"
echo %CD%
```

**macOS/Linux (터미널):**
```bash
cd "프로젝트폴더"
pwd
```

#### 설정 파일 예제 (실제 경로로 변경 필요):

**Windows 예제:**
```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "java",
      "args": [
        "-jar",
        "C:\\Users\\Username\\Documents\\MyMcpServer\\target\\mcp-server-example-1.0.0.jar"
      ]
    }
  }
}
```

**macOS 예제:**
```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/username/Documents/MyMcpServer/target/mcp-server-example-1.0.0.jar"
      ]
    }
  }
}
```

### 4단계: Claude Desktop 재시작

1. Claude Desktop을 완전히 종료
2. 다시 시작
3. 새로운 대화 시작

### 5단계: 연동 확인

Claude Desktop에서 다음과 같이 테스트:

```
MCP 서버의 도구들을 사용할 수 있나요?
```

또는 구체적으로:

```
현재 시간을 알려주세요 (current_time 도구 사용)
```

```
10 + 5를 계산해주세요 (calculator 도구 사용)
```

```
안녕하세요라고 인사해주세요 (greeting 도구 사용)
```

## 문제 해결

### 일반적인 문제들

1. **서버가 인식되지 않는 경우**
   - 절대 경로가 정확한지 확인
   - JAR 파일이 존재하는지 확인
   - Claude Desktop 완전히 재시작

2. **Java 경로 문제**
   ```json
   {
     "mcpServers": {
       "java-mcp-server": {
         "command": "java",
         "args": [...],
         "env": {
           "JAVA_HOME": "/path/to/java"
         }
       }
     }
   }
   ```

3. **권한 문제 (Linux/macOS)**
   ```bash
   chmod +x scripts/run-server.sh
   ```

### 디버깅

#### 서버 로그 확인:
- 프로젝트의 `logs/mcp-server.log` 파일 확인

#### Claude Desktop 로그:
- **Windows**: `%APPDATA%\Claude\logs`
- **macOS**: `~/Library/Logs/Claude`
- **Linux**: `~/.local/share/Claude/logs`

## 사용 가능한 기능

연동 후 Claude에서 다음 기능들을 사용할 수 있습니다:

### 도구 (Tools)
- `current_time`: 현재 시간 조회
- `calculator`: 수학 계산
- `greeting`: 인사 메시지

### 리소스 (Resources)
- `system://info`: 시스템 정보
- `server://status`: 서버 상태
- `config://settings`: 서버 설정

### 프롬프트 (Prompts)
- `code_review`: 코드 리뷰 템플릿
- `document_summary`: 문서 요약 템플릿
- `tech_question`: 기술 질문 답변 템플릿

## 고급 설정

### 여러 MCP 서버 동시 사용:

```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "java",
      "args": ["-jar", "/path/to/java-server.jar"]
    },
    "python-mcp-server": {
      "command": "python",
      "args": ["/path/to/python-server.py"]
    }
  }
}
```

### 환경 변수 설정:

```json
{
  "mcpServers": {
    "java-mcp-server": {
      "command": "java",
      "args": ["-jar", "/path/to/server.jar"],
      "env": {
        "JAVA_HOME": "/path/to/java",
        "LOG_LEVEL": "DEBUG",
        "SERVER_PORT": "8080"
      }
    }
  }
}
```

## 추가 팁

1. **개발 중일 때**: 스크립트를 사용하면 JAR 파일이 자동으로 재빌드됩니다
2. **배포할 때**: 직접 JAR 경로를 지정하는 것이 더 안정적입니다
3. **로깅**: 문제 발생 시 서버 로그와 Claude Desktop 로그를 모두 확인하세요

연동이 성공하면 Claude가 여러분의 Java MCP 서버 기능을 자유롭게 사용할 수 있게 됩니다! 🎉