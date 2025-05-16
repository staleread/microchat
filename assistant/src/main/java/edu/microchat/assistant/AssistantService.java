package edu.microchat.assistant;

import java.util.Arrays;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
class AssistantService {
  private final String repliesQueueName;
  private final RabbitTemplate template;
  private final ChatClient chatClient;

  public AssistantService(
      AssistantAppConfig assistantAppConfig,
      RabbitTemplate template,
      ChatClient chatClient) {
    this.repliesQueueName = assistantAppConfig.assistantRepliesQueueName();
    this.template = template;
    this.chatClient = chatClient;
  }

  public void processPrompt(AssistantPromptDto dto) {
    SystemMessage systemMessage = toSystemMessage(dto.sender());
    var userMessage = new UserMessage(dto.prompt());

    var prompt = new Prompt(Arrays.asList(systemMessage, userMessage));
    String reply = chatClient.prompt(prompt).call().content();

    template.convertAndSend(repliesQueueName, new AssistantReplyDto(reply));
  }

  private SystemMessage toSystemMessage(AssistantPromptDto.MessageSender senderInfo) {
    var content =
        String.format(
            """
        You are a helpful assistant chatting with a user.
        The user's name is %s. Their bio says: "%s".
        Respond as if in a friendly, intelligent conversation.
        """,
            senderInfo.username(), senderInfo.bio());

    return new SystemMessage(content);
  }
}
