package org.agentscope.agentscope2;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.event.ToolCallStartEvent;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.harness.agent.HarnessAgent;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class SteamAgent {
    public static class TimeTools{
        @Tool(name = "get_current_time",description = "获取当前日期时间")
        public String getTime(){
            return LocalDateTime.now().toString();
        }
    }

    public static void main(String[] args) {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new TimeTools());

        HarnessAgent agent = HarnessAgent.builder()
                .model("dashscope:qwen-plus")
                .sysPrompt("你是一个乐于助人的助手，如果用户询问你时间问题请调用时间工具")
                .name("steam-agent")
                .agentId("steam-agent")
                .workspace(Path.of(".agentscope/workspace"))
                .enableAgentTracingLog(false)
                .toolkit(toolkit)
                .build();

        try{
            RuntimeContext ctx = RuntimeContext.builder()
                    .userId("alice").sessionId("stream-demo2").build();

            agent.streamEvents(new UserMessage("现在几点了，今天星期几"),ctx)
                    .doOnNext(event ->{
                        if (event.getType() == AgentEventType.TEXT_BLOCK_DELTA){
                            System.out.print(((TextBlockDeltaEvent) event).getDelta());
                        }else if (event.getType() == AgentEventType.TOOL_CALL_START){
                            System.out.println();
                            System.out.println("[调用工具]"+ ((ToolCallStartEvent) event).getToolCallName());
                        }
                    })
                    .blockLast();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
