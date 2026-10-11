package org.agentscope.agentscope2;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.harness.agent.HarnessAgent;


public class QuickStart{
    private static HarnessAgent.Builder newBuilder() {
        return HarnessAgent.builder()
                .name("note-taker")
                .model("dashscope:qwen-plus")
                .sysPrompt("你是个帮助用户做笔记的助手。")
                .agentId("note-taker");
    }


    public static void main(String[] args) {
        try(HarnessAgent agent = newBuilder().build()){
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId("demon-session")
                    .userId("alice2")
                    .build();
            agent.call(new UserMessage("我是小王，今天准备去篮球场打篮球"),ctx).block();

        }
        try (HarnessAgent agent = newBuilder().build()){
            RuntimeContext ctx = RuntimeContext.builder()
                    .userId("alice2")
                    .sessionId("demo-session")
                    .build();
            agent.call(new UserMessage("我是谁，今天我要干什么"), ctx).block();

        }

    }
}
