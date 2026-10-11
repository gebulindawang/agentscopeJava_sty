package org.agentscope.agentscope2;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.harness.agent.HarnessAgent;
import reactor.core.publisher.Flux;

public class QuickStart {
    public static void main(String[] args) {
//        try (HarnessAgent agent = HarnessAgent.builder()
//                .name("quickStart")
//                .model("dashscope:qwen-plus")
//                .build()) {
//            RuntimeContext ctx = RuntimeContext.builder()
//                    .userId("gebulinking")
//                    .sessionId("session-1")
//                    .build();
////            Msg block = agent.call(Msg.builder().textContent("我是小王").build(),ctx).block();
//            Msg block2 = agent.call(Msg.builder().textContent("我是谁啊").build(),ctx).block();
//            System.out.println(block2.getTextContent());
//        }
        stream();
    }

    public static void stream(){
        try (HarnessAgent agent = HarnessAgent.builder()
                .name("quickStart")
                .model("dashscope:qwen-plus")
                .build()) {
            RuntimeContext ctx = RuntimeContext.builder()
                    .userId("gebulinking")
                    .sessionId("session-1")
                    .build();
//            Msg block = agent.call(Msg.builder().textContent("我是小王").build(),ctx).block();
            agent.streamEvents(Msg.builder().textContent("我是谁啊").build(),ctx)
                    .doOnNext(agentEvent -> {
                        if (agentEvent.getType() == AgentEventType.TEXT_BLOCK_DELTA) {
                            String delta = ((TextBlockDeltaEvent)agentEvent).getDelta();
                            System.out.println(delta);
                        }
                    }).blockLast();
        }
    }
}
