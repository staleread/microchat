package edu.microchat.message.message;

import java.time.LocalDateTime;

record MessageResponse(long id, long senderId, String content, LocalDateTime timestamp) {}
