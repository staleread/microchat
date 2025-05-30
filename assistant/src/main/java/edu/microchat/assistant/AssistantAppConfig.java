package edu.microchat.assistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
class AssistantAppConfig {
  @Value("${microchat.queues.assistant-replies}")
  private String assistantRepliesQueueName;

  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
    return chatClientBuilder.build();
  }

  @Bean
  public Queue assistantPromptsQueue(@Value("${microchat.queues.assistant-prompts}") String name) {
    return new Queue(name);
  }

  @Bean
  public Queue assistantRepliesQueue(@Value("${microchat.queues.assistant-replies}") String name) {
    return new Queue(name);
  }

  @Bean
  public Jackson2JsonMessageConverter jsonConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonConverter) {
    var template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonConverter);

    return template;
  }

  public String assistantRepliesQueueName() {
    return assistantRepliesQueueName;
  }
}
