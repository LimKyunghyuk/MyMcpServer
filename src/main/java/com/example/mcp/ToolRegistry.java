package com.example.mcp;

import com.example.mcp.schema.McpMessage;
import com.example.mcp.schema.McpError;
import com.example.mcp.schema.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MCP 서버에서 사용할 도구들을 등록하고 관리하는 클래스
 */
public class ToolRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ToolRegistry.class);
    
    private final List<Tool> tools;

    public ToolRegistry() {
        this.tools = new ArrayList<>();
        registerTools();
    }

    /**
     * 도구들을 등록합니다.
     */
    private void registerTools() {
        // 현재 시간을 반환하는 도구
        Tool currentTimeTool = new Tool(
            "current_time",
            "현재 날짜와 시간을 반환합니다",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "format", Map.of(
                        "type", "string",
                        "description", "시간 형식 (기본값: yyyy-MM-dd HH:mm:ss)"
                    )
                )
            )
        );
        tools.add(currentTimeTool);

        // 간단한 계산기 도구
        Tool calculatorTool = new Tool(
            "calculator",
            "간단한 수학 계산을 수행합니다 (덧셈, 뺄셈, 곱셈, 나눗셈)",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "a", Map.of(
                        "type", "number",
                        "description", "첫 번째 숫자"
                    ),
                    "b", Map.of(
                        "type", "number",
                        "description", "두 번째 숫자"
                    ),
                    "operation", Map.of(
                        "type", "string",
                        "description", "수행할 연산 (add, subtract, multiply, divide)",
                        "enum", Arrays.asList("add", "subtract", "multiply", "divide")
                    )
                ),
                "required", Arrays.asList("a", "b", "operation")
            )
        );
        tools.add(calculatorTool);

        // 인사 도구
        Tool greetingTool = new Tool(
            "greeting",
            "사용자에게 인사합니다",
            Map.of(
                "type", "object",
                "properties", Map.of(
                    "name", Map.of(
                        "type", "string",
                        "description", "인사할 사람의 이름"
                    ),
                    "language", Map.of(
                        "type", "string",
                        "description", "인사 언어 (korean, english)",
                        "enum", Arrays.asList("korean", "english")
                    )
                ),
                "required", Arrays.asList("name")
            )
        );
        tools.add(greetingTool);

        logger.info("모든 도구가 등록되었습니다. 총 {}개", tools.size());
    }

    public List<Tool> getToolList() {
        return new ArrayList<>(tools);
    }

    @SuppressWarnings("unchecked")
    public McpMessage handleToolCall(Object id, Object params) {
        try {
            Map<String, Object> paramsMap = (Map<String, Object>) params;
            String toolName = (String) paramsMap.get("name");
            Map<String, Object> arguments = (Map<String, Object>) paramsMap.get("arguments");

            if (arguments == null) {
                arguments = new HashMap<>();
            }

            switch (toolName) {
                case "current_time":
                    return handleCurrentTime(id, arguments);
                case "calculator":
                    return handleCalculator(id, arguments);
                case "greeting":
                    return handleGreeting(id, arguments);
                default:
                    return McpMessage.error(id, new McpError(-1, "알 수 없는 도구: " + toolName));
            }

        } catch (Exception e) {
            logger.error("도구 호출 중 오류 발생", e);
            return McpMessage.error(id, new McpError(-1, "도구 실행 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 현재 시간 도구 핸들러
     */
    private McpMessage handleCurrentTime(Object id, Map<String, Object> arguments) {
        try {
            String format = (String) arguments.getOrDefault("format", "yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String currentTime = LocalDateTime.now().format(formatter);

            logger.info("현재 시간을 요청했습니다: {}", currentTime);

            Map<String, Object> result = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "현재 시간: " + currentTime
                )),
                "isError", false
            );

            return McpMessage.response(id, result);

        } catch (Exception e) {
            logger.error("현재 시간 조회 중 오류 발생", e);
            Map<String, Object> result = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "시간 조회 중 오류가 발생했습니다: " + e.getMessage()
                )),
                "isError", true
            );
            return McpMessage.response(id, result);
        }
    }

    /**
     * 계산기 도구 핸들러
     */
    private McpMessage handleCalculator(Object id, Map<String, Object> arguments) {
        try {
            double a = ((Number) arguments.get("a")).doubleValue();
            double b = ((Number) arguments.get("b")).doubleValue();
            String operation = (String) arguments.get("operation");

            double result;
            String operationSymbol;

            switch (operation.toLowerCase()) {
                case "add":
                    result = a + b;
                    operationSymbol = "+";
                    break;
                case "subtract":
                    result = a - b;
                    operationSymbol = "-";
                    break;
                case "multiply":
                    result = a * b;
                    operationSymbol = "*";
                    break;
                case "divide":
                    if (b == 0) {
                        throw new IllegalArgumentException("0으로 나눌 수 없습니다");
                    }
                    result = a / b;
                    operationSymbol = "/";
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 연산입니다: " + operation);
            }

            String resultText = String.format("%.2f %s %.2f = %.2f", a, operationSymbol, b, result);

            logger.info("계산을 수행했습니다: {}", resultText);

            Map<String, Object> resultMap = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", resultText
                )),
                "isError", false
            );

            return McpMessage.response(id, resultMap);

        } catch (Exception e) {
            logger.error("계산 중 오류 발생", e);
            Map<String, Object> resultMap = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "계산 중 오류가 발생했습니다: " + e.getMessage()
                )),
                "isError", true
            );
            return McpMessage.response(id, resultMap);
        }
    }

    /**
     * 인사 도구 핸들러
     */
    private McpMessage handleGreeting(Object id, Map<String, Object> arguments) {
        try {
            String name = (String) arguments.get("name");
            String language = (String) arguments.getOrDefault("language", "korean");

            String greeting;
            if ("english".equalsIgnoreCase(language)) {
                greeting = "Hello, " + name + "! Nice to meet you!";
            } else {
                greeting = "안녕하세요, " + name + "님! 만나서 반갑습니다!";
            }

            logger.info("인사를 전송했습니다: {}", name);

            Map<String, Object> result = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", greeting
                )),
                "isError", false
            );

            return McpMessage.response(id, result);

        } catch (Exception e) {
            logger.error("인사 처리 중 오류 발생", e);
            Map<String, Object> result = Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", "인사 처리 중 오류가 발생했습니다: " + e.getMessage()
                )),
                "isError", true
            );
            return McpMessage.response(id, result);
        }
    }
}