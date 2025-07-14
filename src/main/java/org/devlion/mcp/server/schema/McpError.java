package org.devlion.mcp.server.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MCP 에러 정보
 */
public class McpError {
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private Object data;

    public McpError() {}
    
    public McpError(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public McpError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    // 공통 에러 코드들
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;
    
    // 팩토리 메서드들
    public static McpError parseError() {
        return new McpError(PARSE_ERROR, "Parse error");
    }
    
    public static McpError invalidRequest() {
        return new McpError(INVALID_REQUEST, "Invalid Request");
    }
    
    public static McpError methodNotFound() {
        return new McpError(METHOD_NOT_FOUND, "Method not found");
    }
    
    public static McpError invalidParams() {
        return new McpError(INVALID_PARAMS, "Invalid params");
    }
    
    public static McpError internalError() {
        return new McpError(INTERNAL_ERROR, "Internal error");
    }
}