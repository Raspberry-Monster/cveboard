package dev.raspberrykan.cveboard.models.dao;

import dev.raspberrykan.cveboard.models.enums.Permissions;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserDao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotNull
    private String userName;

    @Column(nullable = false)
    @NotNull
    private String nickName;

    @Column(name = "password_hash", nullable = false)
    @NotNull
    private String password;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Permissions permission = Permissions.User;

    @NotNull
    @Column(nullable = false)
    private Date registerDate;

    @Nullable
    private Date lastLoginDate;
}
