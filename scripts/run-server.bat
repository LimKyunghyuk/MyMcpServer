@echo off
REM Windows용 MCP 서버 실행 스크립트

REM 프로젝트 디렉토리로 이동
cd /d "%~dp0.."

REM 로그 디렉토리 생성
if not exist "logs" mkdir logs

REM Maven으로 JAR 파일 빌드 (없는 경우)
if not exist "target\mcp-server-example-1.0.0.jar" (
    mvn clean package -DskipTests >nul 2>&1
)

REM 서버 실행 (UTF-8 인코딩 설정)
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar target\mcp-server-example-1.0.0.jar