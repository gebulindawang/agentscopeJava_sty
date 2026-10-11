package org.agentscope.agentscope2;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.harness.agent.HarnessAgent;

public class ToolAgent {
    public record UserContext(String username,String vipLevel){}

    public static class WeatherTools{
        @Tool(
                name = "get_weather",
                description = "查询指定城市的天气。当用户询问天气时使用",
                readOnly = true
        )
        public String getWeather(@ToolParam(name = "city",description = "城市名，例如：杭州") String city){
            return  city + "晴天，26℃，东南风2级";
        }
        @Tool(
                name = "get_member_benefit",
                description = "查询当前用户会员等级"
        )
        public String getBenefit(UserContext user){
            if (user ==null) return "匿名用户，请先登录";
            return switch (user.vipLevel()){
                case "gold" -> user.username() + ":生日双倍积分 + 免费该退";
                default -> user.username() + ":基础权益";
            };
        }
        @Tool(
                name = "add",
                description = "计算器，计算式调用这个工具",
                readOnly = true
        )
        public int add(@ToolParam(name = "a",description = "第一个计算参数") int a,@ToolParam(name = "b",description = "第二个计算参数")int b){
            System.out.println("[调用计算工具]");
            return a + b;
        }
    }

    public static void main(String[] args) {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new WeatherTools());

        HarnessAgent.Builder builder = HarnessAgent.builder()
                .name("concierge")
                .agentId("concierge")
                .model("dashscope:qwen-plus")
                .sysPrompt("你是酒店前台主力，回答用户关于天气和会员权益问题")
                .enableAgentTracingLog(false)
                .toolkit(toolkit);

        try(HarnessAgent agent = builder.build()){
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId("tool-demo")
                    .userId("alice")
                    .put(UserContext.class,new UserContext("天宇","gold"))
                    .build();

//            Msg r1 = agent.call(new UserMessage("杭州今天天气怎么样"), ctx).block();
//            System.out.println(r1.getTextContent());
//
//            Msg r2 = agent.call(new UserMessage("我是谁？我有什么会员权益？"), ctx).block();
//            System.out.println(r2.getTextContent());

            Msg r3 = agent.call(new UserMessage("1 + 1 等于多少"), ctx).block();
            System.out.println(r3.getTextContent());
        }
    }
}