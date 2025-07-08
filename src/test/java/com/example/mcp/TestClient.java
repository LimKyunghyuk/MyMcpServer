package com.example.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MCP 서버를 테스트하기 위한 간단한 클라이언트
 */
public class TestClient {
    private static final Logger logger = LoggerFactory.getLogger(TestClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private Process serverProcess;
    private BufferedWriter serverInput;
    private BufferedReader serverOutput;
    private int messageId = 1;

    public static void main(String[] args) {
        TestClient client = new TestClient();
        try {
            client.startServer();
            client.runTests();
        } catch (Exception e) {
            logger.error("테스트 실행 중 오류 발생", e);
        } finally {
            client.stopServer();
        }
    }

    /**
     * MCP 서버 프로세스를 시작합니다.
     */
    public void startServer() throws IOException {
        logger.info("MCP 서버를 시작합니다...");
        
        ProcessBuilder pb = new ProcessBuilder(
            "java", "-cp", "target/classes", "com.example.mcp.McpServerMain"
        );
        pb.redirectErrorStream(false);
        
        serverProcess = pb.start();
        
        serverInput = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
        serverOutput = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
        
        // 서버가 시작될 때까지 잠시 대기
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("서버가 시작되었습니다.");
    }

    /**
     * 서버 프로세스를 종료합니다.
     */
    public void stopServer() {
        if (serverProcess != null) {
            try {
                serverInput.close();
                serverOutput.close();
                serverProcess.destroyForcibly();
                serverProcess.waitFor(5, TimeUnit.SECONDS);
                logger.info("서버가 종료되었습니다.");
            } catch (Exception e) {
                logger.error("서버 종료 중 오류 발생", e);
            }
        }
    }

    /**
     * 다양한 테스트를 실행합니다.
     */
    public void runTests() throws IOException {
        logger.info("테스트를 시작합니다...");
        
        // 1. 초기화 테스트
        testInitialization();
        
        // 2. 도구 목록 조회 테스트
        testListTools();
        
        // 3. 도구 실행 테스트
        testToolExecution();
        
        // 4. 리소스 목록 조회 테스트
        testListResources();
        
        // 5. 리소스 읽기 테스트
        testResourceReading();
        
        // 6. 프롬프트 목록 조회 테스트
        testListPrompts();
        
        logger.info("모든 테스트가 완료되었습니다.");
    }

    /**
     * 초기화 테스트
     */
    private void testInitialization() throws IOException {
        logger.info("=== 초기화 테스트 ===");
        
        Map<String, Object> initMessage = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "initialize",
            "params", Map.of(
                "protocolVersion", "2024-11-05",
                "capabilities", Map.of(
                    "tools", Map.of(),
                    "resources", Map.of(),
                    "prompts", Map.of()
                ),
                "clientInfo", Map.of(
                    "name", "test-client",
                    "version", "1.0.0"
                )
            )
        );
        
        sendMessage(initMessage);
        JsonNode response = receiveMessage();
        logger.info("초기화 응답: {}", response);
    }

    /**
     * 도구 목록 조회 테스트
     */
    private void testListTools() throws IOException {
        logger.info("=== 도구 목록 조회 테스트 ===");
        
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "tools/list"
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("도구 목록: {}", response);
    }

    /**
     * 도구 실행 테스트
     */
    private void testToolExecution() throws IOException {
        logger.info("=== 도구 실행 테스트 ===");
        
        // 현재 시간 도구 테스트
        testCurrentTimeTool();
        
        // 계산기 도구 테스트
        testCalculatorTool();
        
        // 인사 도구 테스트
        testGreetingTool();
    }

    private void testCurrentTimeTool() throws IOException {
        logger.info("--- 현재 시간 도구 테스트 ---");
        
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "tools/call",
            "params", Map.of(
                "name", "current_time",
                "arguments", Map.of("format", "yyyy-MM-dd HH:mm:ss")
            )
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("현재 시간 응답: {}", response);
    }

    private void testCalculatorTool() throws IOException {
        logger.info("--- 계산기 도구 테스트 ---");
        
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "tools/call",
            "params", Map.of(
                "name", "calculator",
                "arguments", Map.of(
                    "a", 10,
                    "b", 5,
                    "operation", "add"
                )
            )
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("계산기 응답: {}", response);
    }

    private void testGreetingTool() throws IOException {
        logger.info("--- 인사 도구 테스트 ---");
        
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "tools/call",
            "params", Map.of(
                "name", "greeting",
                "arguments", Map.of(
                    "name", "개발자",
                    "language", "korean"
                )
            )
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("인사 응답: {}", response);
    }

    /**
     * 리소스 목록 조회 테스트
     */
    private void testListResources() throws IOException {
        logger.info("=== 리소스 목록 조회 테스트 ===");
        
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "resources/list"
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("리소스 목록: {}", response);
    }

    /**
     * 리소스 읽기 테스트
     */
    private void testResourceReading() throws IOException {
        logger.info("=== 리소스 읽기 테스트 ===");
        
        // 시스템 정보 리소스 읽기
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "resources/read",
            "params", Map.of("uri", "system://info")
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("시스템 정보: {}", response);
    }

    /**
     * 프롬프트 목록 조회 테스트
     */
    private void testListPrompts() throws IOException {
        logger.info("=== 프롬프트 목록 조회 테스트 ===");
        
        Map<String, Object> message = Map.of(
            "jsonrpc", "2.0",
            "id", messageId++,
            "method", "prompts/list"
        );
        
        sendMessage(message);
        JsonNode response = receiveMessage();
        logger.info("프롬프트 목록: {}", response);
    }

    /**
     * 서버에 메시지를 전송합니다.
     */
    private void sendMessage(Map<String, Object> message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        logger.debug("전송: {}", json);
        
        serverInput.write(json);
        serverInput.newLine();
        serverInput.flush();
    }

    /**
     * 서버로부터 메시지를 수신합니다.
     */
    private JsonNode receiveMessage() throws IOException {
        String response = serverOutput.readLine();
        if (response == null) {
            throw new IOException("서버로부터 응답을 받지 못했습니다");
        }
        
        logger.debug("수신: {}", response);
        return objectMapper.readTree(response);
    }
}