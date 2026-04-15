package edu.microchat.core.user;

public record UserDto(
    Long id, String username, String firstName, String lastName, String department, String bio) {}
