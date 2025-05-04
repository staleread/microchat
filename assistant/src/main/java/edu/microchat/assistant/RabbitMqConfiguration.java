package edu.microchat.assistant;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMqConfiguration {
  @Bean
  public Queue assistantPromptsQueue(@Value("${microchat.queues.assistant-prompts}") String name) {
    return new Queue(name);
  }

  @Bean
  public Queue assistantRepliesQueue(@Value("${microchat.queues.assistant-replies}") String name) {
    return new Queue(name);
  }
}
