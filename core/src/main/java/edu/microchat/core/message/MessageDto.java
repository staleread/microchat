package edu.microchat.core.message;

import java.time.LocalDateTime;

record MessageDto(
    long id, long senderId, String content, LocalDateTime timestamp, MessageSource source) {}
