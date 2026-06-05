package dev.raspberrykan.cveboard.models.dto.responses;

import dev.raspberrykan.cveboard.models.dao.UserDao;
import dev.raspberrykan.cveboard.models.enums.Permissions;

import java.util.Date;
import java.util.UUID;

public record UserResponse(UUID id, String userName, String nickName, Permissions permission, Date registerDate, Date lastLoginDate) {
    public static UserResponse from(UserDao user) {
        return new UserResponse(user.getId(), user.getUserName(), user.getNickName(), user.getPermission(), user.getRegisterDate(), user.getLastLoginDate());
    }
}
