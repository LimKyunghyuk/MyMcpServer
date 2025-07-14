package com.example.mcp;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

import com.example.mcp.schema.McpMessage;
import com.example.mcp.schema.McpError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MCP 서버의 메인 진입점
 * STDIO 전송을 사용하여 클라이언트와 통신합니다.
 */
public class McpServerMain {
    private static final Logger logger = LoggerFactory.getLogger(McpServerMain.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final ToolRegistry toolRegistry;
    private final ResourceRegistry resourceRegistry;
    private final PromptRegistry promptRegistry;
    private final ExecutorService executor;
    
    private final Map<String, Object> serverInfo;
    private final Map<String, Object> serverCapabilities;

    public McpServerMain() {
        // ObjectMapper UTF-8 설정 - ASCII 이스케이프 활성화로 한글 문제 방지
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        
        this.toolRegistry = new ToolRegistry();
        this.resourceRegistry = new ResourceRegistry();
        this.promptRegistry = new PromptRegistry();
        this.executor = Executors.newCachedThreadPool();
        
        // 로그 디렉토리 생성
        java.io.File logDir = new java.io.File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        // 서버 정보 설정
        this.serverInfo = Map.of(
            "name", "example-mcp-server",
            "version", "1.0.0"
        );
        
        // 서버 역량 설정
        this.serverCapabilities = Map.of(
            "tools", Map.of("listChanged", false),
            "resources", Map.of("listChanged", false),
            "prompts", Map.of("listChanged", false),
            "logging", Map.of()
        );
    }

    public static void main(String[] args) {

        McpServerMain server = new McpServerMain();
        try {
            server.start();
        } catch (Exception e) {
            logger.error("서버 시작 중 오류가 발생했습니다", e);
            System.exit(1);
        }
    }
    
    public void start() throws IOException {
        // 시작 메시지도 로그 파일에만 기록
        logger.info("MCP 서버가 시작되었습니다. 클라이언트 연결을 기다리는 중...");
        // 표준입출력
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true); // 자동 플러시 활성화
        
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim(); // 공백 문자 제거
            if (line.isEmpty()) {
                continue; // 빈 줄 무시
            }
            
            try {
                McpMessage request = objectMapper.readValue(line, McpMessage.class);
                handleRequest(request, writer); // 동기 처리로 변경
            } catch (Exception e) {
                logger.error("요청 처리 중 오류 발생: {}", line, e);
                McpMessage errorResponse = McpMessage.error(null, McpError.parseError());
                sendResponse(errorResponse, writer);
            }
        }
    }
    
    private void handleRequest(McpMessage request, PrintWriter writer) {
        try {
            String method = request.getMethod();
            Object id = request.getId();
            
            logger.debug("요청 처리: method={}, id={}", method, id);
            
            McpMessage response;
            
            switch (method) {
                case "initialize":
                    response = handleInitialize(id, request.getParams());
                    break;
                case "notifications/initialized":
                    // 초기화 완료 알림은 응답하지 않음
                    return;
                case "tools/list":
                    response = handleListTools(id);
                    break;
                case "tools/call":
                    response = handleToolCall(id, request.getParams());
                    break;
                case "resources/list":
                    response = handleListResources(id);
                    break;
                case "resources/read":
                    response = handleResourceRead(id, request.getParams());
                    break;
                case "prompts/list":
                    response = handleListPrompts(id);
                    break;
                case "prompts/get":
                    response = handlePromptGet(id, request.getParams());
                    break;
                default:
                    logger.warn("알 수 없는 메서드: {}", method);
                    response = McpMessage.error(id, McpError.methodNotFound());
            }
            
            sendResponse(response, writer);
            
        } catch (Exception e) {
            logger.error("요청 핸들링 중 오류 발생", e);
            McpMessage errorResponse = McpMessage.error(request.getId(), McpError.internalError());
            sendResponse(errorResponse, writer);
        }
    }
    
    private McpMessage handleInitialize(Object id, Object params) {
        logger.info("클라이언트 초기화 요청을 받았습니다");
        
        Map<String, Object> result = Map.of(
            "protocolVersion", "2024-11-05",
            "capabilities", serverCapabilities,
            "serverInfo", serverInfo
        );
        
        return McpMessage.response(id, result);
    }
    
    private McpMessage handleListTools(Object id) {
        Map<String, Object> result = Map.of(
            "tools", toolRegistry.getToolList()
        );
        return McpMessage.response(id, result);
    }
    
    private McpMessage handleToolCall(Object id, Object params) {
        return toolRegistry.handleToolCall(id, params);
    }
    
    private McpMessage handleListResources(Object id) {
        Map<String, Object> result = Map.of(
            "resources", resourceRegistry.getResourceList()
        );
        return McpMessage.response(id, result);
    }
    
    private McpMessage handleResourceRead(Object id, Object params) {
        return resourceRegistry.handleResourceRead(id, params);
    }
    
    private McpMessage handleListPrompts(Object id) {
        Map<String, Object> result = Map.of(
            "prompts", promptRegistry.getPromptList()
        );
        return McpMessage.response(id, result);
    }
    
    private McpMessage handlePromptGet(Object id, Object params) {
        return promptRegistry.handlePromptGet(id, params);
    }
    
    private synchronized void sendResponse(McpMessage response, PrintWriter writer) {
        try {
            String json = objectMapper.writeValueAsString(response);
            
            // 디버그를 위해 JSON 길이 확인
            logger.debug("응답 전송 (길이: {}): {}", json.length(), json.substring(0, Math.min(100, json.length())));
            
            // JSON 메시지를 한 줄로 출력하고 즉시 플러시
            writer.println(json); // println 사용으로 개행 문자 확실히 추가
            writer.flush(); // 명시적 플러시
            
        } catch (Exception e) {
            logger.error("응답 전송 중 오류 발생", e);
        }
    }
}