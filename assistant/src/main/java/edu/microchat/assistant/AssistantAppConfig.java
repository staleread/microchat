package edu.microchat.assistant;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AssistantAppConfig {
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
}
