package org.devlion.mcp.server.prompt;

import org.devlion.mcp.server.schema.McpError;
import org.devlion.mcp.server.schema.McpMessage;
import org.devlion.mcp.server.schema.Prompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP 서버에서 사용할 프롬프트들을 등록하고 관리하는 클래스
 */
public class PromptRegistry {
    private static final Logger logger = LoggerFactory.getLogger(PromptRegistry.class);
    
    private final List<Prompt> prompts;

    public PromptRegistry() {
        this.prompts = new ArrayList<>();
        registerPrompts();
    }

    /**
     * 프롬프트들을 등록합니다.
     */
    private void registerPrompts() {
        // 코드 리뷰 프롬프트
        Prompt codeReview = new Prompt(
            "code_review",
            "코드 리뷰를 위한 구조화된 프롬프트를 제공합니다",
            List.of(
                new Prompt.PromptArgument("code", "리뷰할 코드", true),
                new Prompt.PromptArgument("language", "프로그래밍 언어 (예: java, python, javascript)", true),
                new Prompt.PromptArgument("focus_areas", "중점적으로 검토할 영역 (예: performance, security, readability)", false)
            )
        );
        prompts.add(codeReview);

        // 문서 요약 프롬프트
        Prompt documentSummary = new Prompt(
            "document_summary",
            "문서 요약을 위한 프롬프트를 제공합니다",
            List.of(
                new Prompt.PromptArgument("document", "요약할 문서 내용", true),
                new Prompt.PromptArgument("summary_length", "요약 길이 (short, medium, long)", false),
                new Prompt.PromptArgument("key_points", "강조할 핵심 포인트 수", false)
            )
        );
        prompts.add(documentSummary);

        // 기술 질문 프롬프트
        Prompt techQuestion = new Prompt(
            "tech_question",
            "기술적 질문에 대한 구조화된 답변을 위한 프롬프트를 제공합니다",
            List.of(
                new Prompt.PromptArgument("question", "기술적 질문", true),
                new Prompt.PromptArgument("domain", "기술 도메인 (예: backend, frontend, database, devops)", true),
                new Prompt.PromptArgument("experience_level", "경험 수준 (beginner, intermediate, advanced)", false)
            )
        );
        prompts.add(techQuestion);

        logger.info("모든 프롬프트가 등록되었습니다. 총 {}개", prompts.size());
    }

    public List<Prompt> getPromptList() {
        return new ArrayList<>(prompts);
    }

    @SuppressWarnings("unchecked")
    public McpMessage handlePromptGet(Object id, Object params) {
        try {
            Map<String, Object> paramsMap = (Map<String, Object>) params;
            String promptName = (String) paramsMap.get("name");
            Map<String, Object> arguments = (Map<String, Object>) paramsMap.get("arguments");

            if (arguments == null) {
                arguments = new HashMap<>();
            }

            switch (promptName) {
                case "code_review":
                    return handleCodeReviewPrompt(id, arguments);
                case "document_summary":
                    return handleDocumentSummaryPrompt(id, arguments);
                case "tech_question":
                    return handleTechQuestionPrompt(id, arguments);
                default:
                    return McpMessage.error(id, new McpError(-1, "알 수 없는 프롬프트: " + promptName));
            }

        } catch (Exception e) {
            logger.error("프롬프트 처리 중 오류 발생", e);
            return McpMessage.error(id, new McpError(-1, "프롬프트 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 코드 리뷰 프롬프트 핸들러
     */
    private McpMessage handleCodeReviewPrompt(Object id, Map<String, Object> arguments) {
        try {
            String code = (String) arguments.get("code");
            String language = (String) arguments.get("language");
            String focusAreas = (String) arguments.getOrDefault("focus_areas", "전반적인 코드 품질");

            String promptContent = "다음 " + language + " 코드를 리뷰해주세요:\n\n" +
                    "```" + language + "\n" + code + "\n```\n\n" +
                    "리뷰 중점 사항: " + focusAreas + "\n\n" +
                    "다음 형식으로 리뷰를 제공해주세요:\n\n" +
                    "## 코드 리뷰 결과\n\n" +
                    "### 긍정적인 부분\n- [좋은 점들을 나열]\n\n" +
                    "### 개선이 필요한 부분\n- [문제점과 개선 방안]\n\n" +
                    "### 보안 검토\n- [보안 관련 이슈 및 권장사항]\n\n" +
                    "### 성능 검토\n- [성능 최적화 제안]\n\n" +
                    "### 가독성 및 유지보수성\n- [코드 구조 및 명명 개선 제안]\n\n" +
                    "### 전체 평가\n- 점수: __/10\n- 주요 권장사항: [핵심 개선 포인트]";

            logger.info("코드 리뷰 프롬프트를 생성했습니다 (언어: {})", language);

            Map<String, Object> result = Map.of(
                "description", "코드 리뷰를 위한 구조화된 프롬프트",
                "messages", List.of(Map.of(
                    "role", "user",
                    "content", Map.of(
                        "type", "text",
                        "text", promptContent
                    )
                ))
            );

            return McpMessage.response(id, result);

        } catch (Exception e) {
            logger.error("코드 리뷰 프롬프트 생성 중 오류 발생", e);
            Map<String, Object> result = Map.of(
                "description", "오류가 발생했습니다",
                "messages", List.of(Map.of(
                    "role", "user",
                    "content", Map.of(
                        "type", "text",
                        "text", "코드 리뷰 프롬프트 생성 중 오류가 발생했습니다: " + e.getMessage()
                    )
                ))
            );
            return McpMessage.response(id, result);
        }
    }

    /**
     * 문서 요약 프롬프트 핸들러 (간단한 구현)
     */
    private McpMessage handleDocumentSummaryPrompt(Object id, Map<String, Object> arguments) {
        logger.info("문서 요약 프롬프트를 생성했습니다");
        
        Map<String, Object> result = Map.of(
            "description", "문서 요약을 위한 구조화된 프롬프트",
            "messages", List.of(Map.of(
                "role", "user",
                "content", Map.of(
                    "type", "text",
                    "text", "문서 요약 프롬프트가 생성되었습니다."
                )
            ))
        );
        
        return McpMessage.response(id, result);
    }

    /**
     * 기술 질문 프롬프트 핸들러 (간단한 구현)
     */
    private McpMessage handleTechQuestionPrompt(Object id, Map<String, Object> arguments) {
        logger.info("기술 질문 프롬프트를 생성했습니다");
        
        Map<String, Object> result = Map.of(
            "description", "기술 질문에 대한 구조화된 답변을 위한 프롬프트",
            "messages", List.of(Map.of(
                "role", "user",
                "content", Map.of(
                    "type", "text",
                    "text", "기술 질문 프롬프트가 생성되었습니다."
                )
            ))
        );
        
        return McpMessage.response(id, result);
    }
}