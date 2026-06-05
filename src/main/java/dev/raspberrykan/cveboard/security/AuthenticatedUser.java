package dev.raspberrykan.cveboard.security;

import dev.raspberrykan.cveboard.models.dao.UserDao;
import dev.raspberrykan.cveboard.models.enums.Permissions;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AuthenticatedUser implements UserDetails {
    @Getter
    private final UUID id;
    private final String username;
    private final String password;
    @Getter
    private final Permissions permission;

    public AuthenticatedUser(UserDao user) {
        this.id = user.getId();
        this.username = user.getUserName();
        this.password = user.getPassword();
        this.permission = user.getPermission();
    }


    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(permission.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return username;
    }
}
