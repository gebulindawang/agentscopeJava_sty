package org.agentscope.agentscope2;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.harness.agent.HarnessAgent;


public class QuickStart{
    private static final HarnessAgent.Builder BUILDER = HarnessAgent.builder()
            .name("note-taker")
            .model("dashscope:qwen-plus")
            .sysPrompt("你是个帮助用户做笔记的助手。")
            .agentId("note-taker");


    public static void main(String[] args) {
        try(HarnessAgent agent = BUILDER.build()){
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId("demon-session")
                    .userId("alice")
                    .build();
            Msg reply =agent.call(new UserMessage("我是小王，今天准备一个技术分享"),ctx).block();

        }
        try (HarnessAgent agent = BUILDER.build()){
            RuntimeContext ctx = RuntimeContext.builder()
                    .userId("alice")
                    .sessionId("demo-session")
                    .build();
            Msg reply = agent.call(new UserMessage("我是谁，今天我要干什么"), ctx).block();

        }

    }
}
