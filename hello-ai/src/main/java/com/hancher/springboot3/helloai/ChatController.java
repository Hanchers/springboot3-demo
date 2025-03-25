package com.hancher.springboot3.helloai;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ai/chat")
public class ChatController {

    private final OpenAiChatModel chatModel;

    @Autowired
    public ChatController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
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
}
