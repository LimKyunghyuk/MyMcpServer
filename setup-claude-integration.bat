@echo off
REM Claude Desktop과 MCP 서버 연동을 위한 설정 스크립트 (Windows)

echo === Claude Desktop MCP 서버 연동 설정 ===

REM 현재 프로젝트 경로 가져오기
set PROJECT_PATH=%CD%
set JAR_PATH=%PROJECT_PATH%\target\mcp-server-example-1.0.0.jar
set SCRIPT_PATH=%PROJECT_PATH%\scripts\run-server.bat

echo 프로젝트 경로: %PROJECT_PATH%

REM 1. JAR 파일 빌드
echo.
echo 1. JAR 파일 빌드 중...
mvn clean package -DskipTests

if not exist "%JAR_PATH%" (
    echo ❌ JAR 파일 빌드에 실패했습니다.
    pause
    exit /b 1
)

echo ✅ JAR 파일이 성공적으로 빌드되었습니다.

REM 2. Claude Desktop 설정 파일 경로
echo.
echo 2. Claude Desktop 설정 파일 경로:
set CLAUDE_CONFIG_PATH=%APPDATA%\Claude\claude_desktop_config.json
echo Windows 감지됨
echo 설정 파일 경로: %CLAUDE_CONFIG_PATH%

REM 3. 설정 파일 디렉토리 생성
echo.
echo 3. Claude Desktop 설정 디렉토리 생성 중...
if not exist "%APPDATA%\Claude\" mkdir "%APPDATA%\Claude\"

REM 4. 설정 파일 생성 (기존 파일이 있으면 백업)
echo.
echo 4. Claude Desktop 설정 파일 생성 중...

if exist "%CLAUDE_CONFIG_PATH%" (
    echo ⚠️  기존 설정 파일이 발견되었습니다.
    echo 백업을 생성합니다: %CLAUDE_CONFIG_PATH%.backup
    copy "%CLAUDE_CONFIG_PATH%" "%CLAUDE_CONFIG_PATH%.backup" >nul
)

REM 설정 파일 내용 생성
(
echo {
echo   "mcpServers": {
echo     "java-mcp-server": {
echo       "command": "cmd",
echo       "args": [
echo         "/c",
echo         "%SCRIPT_PATH:\=\\%"
echo       ]
echo     }
echo   }
echo }
) > "%CLAUDE_CONFIG_PATH%"

echo ✅ Claude Desktop 설정 파일이 생성되었습니다.

REM 5. 설정 파일 내용 표시
echo.
echo 5. 생성된 설정 파일 내용:
echo ----------------------------------------
type "%CLAUDE_CONFIG_PATH%"
echo ----------------------------------------

REM 6. 최종 안내
echo.
echo === 설정 완료! ===
echo.
echo 다음 단계를 수행하세요:
echo 1. Claude Desktop을 완전히 종료하세요
echo 2. Claude Desktop을 다시 시작하세요
echo 3. 새로운 대화에서 다음과 같이 테스트하세요:
echo    '현재 시간을 알려주세요'
echo    '10 + 5를 계산해주세요'
echo    '안녕하세요라고 인사해주세요'
echo.
echo 문제가 발생하면 다음 로그를 확인하세요:
echo - 서버 로그: %PROJECT_PATH%\logs\mcp-server.log
echo - Claude Desktop 로그: %APPDATA%\Claude\logs
echo.
echo 🎉 연동 설정이 완료되었습니다!
echo.
pause