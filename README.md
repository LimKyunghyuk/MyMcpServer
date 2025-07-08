# MCP Server Example

이 프로젝트는 Java로 직접 구현한 Model Context Protocol (MCP) 서버 예제입니다.

## 개요

MCP (Model Context Protocol)는 AI 모델과 도구들 간의 표준화된 인터페이스를 제공하는 프로토콜입니다. 이 서버는 다음과 같은 기능을 제공합니다:

- **도구 (Tools)**: 현재 시간 조회, 계산기, 인사 기능
- **리소스 (Resources)**: 시스템 정보, 서버 상태, 설정 정보
- **프롬프트 (Prompts)**: 코드 리뷰, 문서 요약, 기술 질문 템플릿
- **로깅**: 구조화된 로그 메시지 지원

## 요구사항

- Java 17 이상
- Maven 3.6 이상
- IntelliJ IDEA (권장)

## 빌드 및 실행

### 1. IntelliJ에서 프로젝트 열기

1. IntelliJ IDEA를 실행합니다
2. 프로젝트 폴더를 엽니다
3. Maven 의존성이 자동으로 다운로드됩니다

### 2. 서버 실행

#### IntelliJ에서 실행:
1. `src/main/java/com/example/mcp/McpServerMain.java` 파일을 엽니다
2. 클래스 이름 옆의 녹색 화살표를 클릭하거나 `Ctrl+Shift+F10`을 누릅니다

#### 터미널에서 실행:
```bash
# Maven을 통한 실행
mvn exec:java -Dexec.mainClass="com.example.mcp.McpServerMain"

# 또는 JAR 파일 생성 후 실행
mvn clean package
java -jar target/mcp-server-example-1.0.0.jar
```

### 3. 테스트

테스트 클라이언트를 실행하여 서버 기능을 확인:

```bash
# 테스트 클라이언트 실행
mvn exec:java -Dexec.mainClass="com.example.mcp.TestClient"
```

또는 IntelliJ에서 `src/test/java/com/example/mcp/TestClient.java`를 직접 실행할 수 있습니다.

## 제공되는 기능

### 도구 (Tools)

1. **current_time**: 현재 날짜와 시간을 반환
   - 매개변수: `format` (선택사항) - 시간 형식 지정
   
2. **calculator**: 기본 수학 계산 수행
   - 매개변수: `a` (숫자), `b` (숫자), `operation` (add/subtract/multiply/divide)
   
3. **greeting**: 사용자에게 인사
   - 매개변수: `name` (이름), `language` (korean/english, 선택사항)

### 리소스 (Resources)

1. **system://info**: 시스템 정보 (OS, Java 버전, 메모리 사용량 등)
2. **server://status**: 서버 상태 정보
3. **config://settings**: 서버 설정 정보

### 프롬프트 (Prompts)

1. **code_review**: 코드 리뷰를 위한 구조화된 프롬프트
2. **document_summary**: 문서 요약을 위한 프롬프트
3. **tech_question**: 기술 질문 답변을 위한 프롬프트

## 프로젝트 구조

```
MyMcpServer/
├── src/
│   ├── main/
│   │   ├── java/com/example/mcp/
│   │   │   ├── McpServerMain.java          # 메인 서버 클래스
│   │   │   ├── ToolRegistry.java           # 도구 등록 및 관리
│   │   │   ├── ResourceRegistry.java       # 리소스 등록 및 관리
│   │   │   ├── PromptRegistry.java         # 프롬프트 등록 및 관리
│   │   │   └── schema/                     # MCP 스키마 클래스들
│   │   │       ├── McpMessage.java         # JSON-RPC 메시지
│   │   │       ├── McpError.java           # 에러 정의
│   │   │       ├── Tool.java               # 도구 스키마
│   │   │       ├── Resource.java           # 리소스 스키마
│   │   │       └── Prompt.java             # 프롬프트 스키마
│   │   └── resources/
│   │       └── logback.xml                 # 로깅 설정
│   └── test/
│       └── java/com/example/mcp/
│           └── TestClient.java             # 테스트 클라이언트
├── pom.xml                                 # Maven 설정
├── README.md                               # 프로젝트 설명서
└── .gitignore                              # Git 무시 파일
```

## MCP 프로토콜 구현

이 프로젝트는 MCP Java SDK 대신 직접 구현된 코드를 사용합니다:

- **JSON-RPC 2.0**: 표준 JSON-RPC 프로토콜로 통신
- **STDIO 전송**: 표준 입출력을 통한 클라이언트-서버 통신
- **비동기 처리**: 요청을 비동기적으로 처리
- **에러 핸들링**: 표준 JSON-RPC 에러 코드 지원

## 설정

### 로깅 설정

`src/main/resources/logback.xml` 파일에서 로깅 레벨과 출력 형식을 조정할 수 있습니다:

- 콘솔 출력과 파일 출력 모두 지원
- 일별 로그 파일 롤링
- 최대 파일 크기 및 보관 기간 설정

### 서버 설정

서버의 기본 설정은 `McpServerMain` 클래스에서 변경할 수 있습니다:

- 서버 이름 및 버전
- 지원할 기능 (tools, resources, prompts, logging)

## 확장 방법

### 새로운 도구 추가

1. `ToolRegistry.java`에서 새로운 `Tool` 객체를 `tools` 리스트에 추가
2. `handleToolCall()` 메서드에 새로운 케이스 추가
3. 해당 도구의 핸들러 메서드 구현

### 새로운 리소스 추가

1. `ResourceRegistry.java`에서 새로운 `Resource` 객체를 `resources` 리스트에 추가
2. `handleResourceRead()` 메서드에 새로운 케이스 추가
3. 해당 리소스의 핸들러 메서드 구현

### 새로운 프롬프트 추가

1. `PromptRegistry.java`에서 새로운 `Prompt` 객체를 `prompts` 리스트에 추가
2. `handlePromptGet()` 메서드에 새로운 케이스 추가
3. 해당 프롬프트의 핸들러 메서드 구현

## 클라이언트 연결

이 MCP 서버는 STDIO(표준 입출력)를 통해 클라이언트와 통신합니다:

### JSON-RPC 메시지 예제

**초기화 요청:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2024-11-05",
    "capabilities": {},
    "clientInfo": {"name": "test-client", "version": "1.0.0"}
  }
}
```

**도구 호출 요청:**
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/call",
  "params": {
    "name": "calculator",
    "arguments": {"a": 10, "b": 5, "operation": "add"}
  }
}
```

## 트러블슈팅

### 일반적인 문제

1. **의존성 문제**
   - IntelliJ에서 Maven 새로고침: `Ctrl+Shift+O`
   - 터미널에서: `mvn dependency:resolve`

2. **서버가 시작되지 않는 경우**
   - Java 17 이상이 설치되어 있는지 확인
   - IntelliJ 프로젝트 SDK 설정 확인

3. **로그 파일이 생성되지 않는 경우**
   - `logs/` 디렉토리 생성: `mkdir logs`
   - 파일 쓰기 권한 확인

### 디버깅

디버깅을 위해 로깅 레벨을 DEBUG로 변경:

```xml
<!-- logback.xml에서 -->
<logger name="com.example.mcp" level="DEBUG" />
```

## 참고 자료

- [MCP 공식 사이트](https://modelcontextprotocol.io/)
- [MCP 사양](https://modelcontextprotocol.io/specification/)
- [JSON-RPC 2.0 사양](https://www.jsonrpc.org/specification)

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.