package org.agentscope.agentscope2;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.harness.agent.HarnessAgent;

import java.util.List;

public class StructuredAgent {

    public record MovieReview(String title,int score,String oneLineSummary){}

    public static void main(String[] args) {
        DashScopeChatModel qwen = DashScopeChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .nativeStructuredOutput(true)
                .modelName("qwen-plus")
                .build();

        HarnessAgent.Builder builder = HarnessAgent.builder()
                .name("critic")
                .agentId("critic")
                .model(qwen)
                .enableAgentTracingLog(false)
                .sysPrompt("你是电影评论员，评论要犀利但是公正,并且最后要用一句话概括");

        try(HarnessAgent agent = builder.build()){
            RuntimeContext ctx = RuntimeContext.builder()
                    .userId("alice")
                    .sessionId("struct-demo")
                    .build();

            Msg result = agent.call(List.of(new UserMessage("评论一下《流浪地球2》")), MovieReview.class,ctx).block();
            if (result != null) {
                MovieReview review = result.getStructuredData(MovieReview.class);
                System.out.println("片名: " + review.title());
                System.out.println("评分: " + review.score());
                System.out.println("一句话: " + review.oneLineSummary());
            }
        }
    }
}
