package com.hancher.springboot3.helloai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/ai/chat")
public class ChatController {

    private final OpenAiChatModel chatModel;
    private final ChatClient chatClient;

    @Autowired
    public ChatController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.builder(chatModel)
                // 可以给系统设定角色
                .defaultSystem("你是一名AI助理，你的名字叫维纳斯，可以解决我的任何问题")
                .build();
    }

    /**
     * 阻塞式接口，可以使用普通的web框架，会等deepseek回答完成后一次性返回
     * @param message 问题
     * @return
     */
    @GetMapping("/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String uuid = UUID.randomUUID().toString();
        System.out.printf("uid=%s, message: %s%n", uuid, message);
        String res = this.chatModel.call(message);
        System.out.printf("uid=%s, message: %s%n", uuid, res);
        return Map.of("generation", res);
    }
    /**
     * 流式交互接口，会一个字一个字的返回数据，需要使用响应式web框架
     * @param message 问题
     * @return
     */
    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }



    /**
     * 阻塞式接口，可以使用普通的web框架，会等deepseek回答完成后一次性返回
     * @param message 问题
     * @return
     */
    @GetMapping("/ask")
    public Map ask(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String uuid = UUID.randomUUID().toString();
        System.out.printf("uid=%s, message: %s%n", uuid, message);
        String res = this.chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user(message)
                .call()
                .content();

        System.out.printf("uid=%s, message: %s%n", uuid, res);
        return Map.of("generation", res);
    }
    /**
     * 流式交互接口，会一个字一个字的返回数据，需要使用响应式web框架
     * @param message 问题
     * @return
     */
    @GetMapping(value = "/askStream",produces = MediaType.TEXT_HTML_VALUE+";charset=UTF-8")
    public Flux<String> askStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Flux<String> content = this.chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user(message)
                .stream()
                .content();

//        return content.doOnNext(response -> System.out.println("askStream res:" + response));
        return content;
    }
}
