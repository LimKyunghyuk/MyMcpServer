package com.example.mcp.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * MCP JSON-RPC 메시지의 기본 클래스
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpMessage {
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    @JsonProperty("id")
    private Object id;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private Object params;
    
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private McpError error;

    // 생성자
    public McpMessage() {}
    
    public McpMessage(Object id, String method, Object params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
    
    public McpMessage(Object id, Object result) {
        this.id = id;
        this.result = result;
    }

    // Getters and Setters
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public McpError getError() {
        return error;
    }

    public void setError(McpError error) {
        this.error = error;
    }
    
    // 팩토리 메서드들
    public static McpMessage request(Object id, String method, Object params) {
        return new McpMessage(id, method, params);
    }
    
    public static McpMessage response(Object id, Object result) {
        return new McpMessage(id, result);
    }
    
    public static McpMessage error(Object id, McpError error) {
        McpMessage message = new McpMessage();
        message.setId(id);
        message.setError(error);
        return message;
    }
}