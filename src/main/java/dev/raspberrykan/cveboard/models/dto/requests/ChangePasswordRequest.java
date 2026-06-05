package dev.raspberrykan.cveboard.models.dto.requests;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
