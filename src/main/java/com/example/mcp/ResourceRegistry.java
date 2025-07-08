package com.example.mcp;

import com.example.mcp.schema.McpMessage;
import com.example.mcp.schema.McpError;
import com.example.mcp.schema.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * MCP 서버에서 사용할 리소스들을 등록하고 관리하는 클래스
 */
public class ResourceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ResourceRegistry.class);
    
    private final List<Resource> resources;

    public ResourceRegistry() {
        this.resources = new ArrayList<>();
        registerResources();
    }

    /**
     * 리소스들을 등록합니다.
     */
    private void registerResources() {
        // 시스템 정보 리소스
        Resource systemInfo = new Resource(
            "system://info",
            "시스템 정보",
            "현재 시스템의 기본 정보를 제공합니다",
            "application/json"
        );
        resources.add(systemInfo);

        // 서버 상태 리소스
        Resource serverStatus = new Resource(
            "server://status",
            "서버 상태",
            "MCP 서버의 현재 상태 정보를 제공합니다",
            "application/json"
        );
        resources.add(serverStatus);

        // 설정 리소스
        Resource config = new Resource(
            "config://settings",
            "서버 설정",
            "서버 설정 정보를 제공합니다",
            "application/json"
        );
        resources.add(config);

        logger.info("모든 리소스가 등록되었습니다. 총 {}개", resources.size());
    }

    public List<Resource> getResourceList() {
        return new ArrayList<>(resources);
    }

    @SuppressWarnings("unchecked")
    public McpMessage handleResourceRead(Object id, Object params) {
        try {
            Map<String, Object> paramsMap = (Map<String, Object>) params;
            String uri = (String) paramsMap.get("uri");

            switch (uri) {
                case "system://info":
                    return handleSystemInfo(id);
                case "server://status":
                    return handleServerStatus(id);
                case "config://settings":
                    return handleConfig(id);
                default:
                    return McpMessage.error(id, new McpError(-1, "알 수 없는 리소스: " + uri));
            }

        } catch (Exception e) {
            logger.error("리소스 읽기 중 오류 발생", e);
            return McpMessage.error(id, new McpError(-1, "리소스 읽기 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 시스템 정보 리소스 핸들러
     */
    private McpMessage handleSystemInfo(Object id) {
        try {
            // 시스템 정보 수집
            Map<String, Object> systemInfo = Map.of(
                "java_version", System.getProperty("java.version"),
                "java_vendor", System.getProperty("java.vendor"),
                "os_name", System.getProperty("os.name"),
                "os_version", System.getProperty("os.version"),
                "os_arch", System.getProperty("os.arch"),
                "available_processors", Runtime.getRuntime().availableProcessors(),
                "max_memory", Runtime.getRuntime().maxMemory(),
                "total_memory", Runtime.getRuntime().totalMemory(),
                "free_memory", Runtime.getRuntime().freeMemory(),
                "timestamp", LocalDateTime.now().toString()
            );

            String jsonContent = convertToJson(systemInfo);

            logger.info("시스템 정보를 조회했습니다");

            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "type", "text",
                    "text", jsonContent
                ))
            );

            return McpMessage.response(id, result);

        } catch (Exception e) {
            logger.error("시스템 정보 조회 중 오류 발생", e);
            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "type", "text",
                    "text", "{\"error\": \"시스템 정보 조회 중 오류가 발생했습니다: " + e.getMessage() + "\"}"
                ))
            );
            return McpMessage.response(id, result);
        }
    }

    /**
     * 서버 상태 리소스 핸들러
     */
    private McpMessage handleServerStatus(Object id) {
        try {
            Map<String, Object> serverStatus = Map.of(
                "server_name", "example-mcp-server",
                "version", "1.0.0",
                "status", "running",
                "uptime_ms", System.currentTimeMillis(),
                "active_connections", 1,
                "supported_capabilities", List.of("tools", "resources", "prompts", "logging"),
                "timestamp", LocalDateTime.now().toString()
            );

            String jsonContent = convertToJson(serverStatus);

            logger.info("서버 상태를 조회했습니다");

            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "type", "text",
                    "text", jsonContent
                ))
            );

            return McpMessage.response(id, result);

        } catch (Exception e) {
            logger.error("서버 상태 조회 중 오류 발생", e);
            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "type", "text",
                    "text", "{\"error\": \"서버 상태 조회 중 오류가 발생했습니다: " + e.getMessage() + "\"}"
                ))
            );
            return McpMessage.response(id, result);
        }
    }

    /**
     * 설정 리소스 핸들러
     */
    private McpMessage handleConfig(Object id) {
        try {
            Map<String, Object> config = Map.of(
                "server_config", Map.of(
                    "max_concurrent_requests", 100,
                    "request_timeout_ms", 30000,
                    "log_level", "INFO",
                    "enable_debug", false
                ),
                "tools_config", Map.of(
                    "enabled_tools", List.of("current_time", "calculator", "greeting"),
                    "max_tool_execution_time_ms", 10000
                ),
                "resources_config", Map.of(
                    "max_resource_size_bytes", 1048576,
                    "cache_enabled", true
                ),
                "timestamp", LocalDateTime.now().toString()
            );

            String jsonContent = convertToJson(config);

            logger.info("설정 정보를 조회했습니다");

            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "type", "text",
                    "text", jsonContent
                ))
            );

            return McpMessage.response(id, result);

        } catch (Exception e) {
            logger.error("설정 조회 중 오류 발생", e);
            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "type", "text",
                    "text", "{\"error\": \"설정 조회 중 오류가 발생했습니다: " + e.getMessage() + "\"}"
                ))
            );
            return McpMessage.response(id, result);
        }
    }

    /**
     * 객체를 JSON 문자열로 변환합니다 (간단한 구현)
     */
    private String convertToJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else if (value instanceof List) {
                json.append("[");
                List<?> list = (List<?>) value;
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) json.append(",");
                    Object item = list.get(i);
                    if (item instanceof String) {
                        json.append("\"").append(item).append("\"");
                    } else {
                        json.append(item);
                    }
                }
                json.append("]");
            } else if (value instanceof Map) {
                json.append(convertToJson((Map<String, Object>) value));
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            first = false;
        }
        
        json.append("}");
        return json.toString();
    }
}