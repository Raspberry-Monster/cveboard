package dev.raspberrykan.cveboard.controllers;

import dev.raspberrykan.cveboard.models.dto.requests.ChangePasswordRequest;
import dev.raspberrykan.cveboard.models.dto.requests.UpdateUserInfoRequest;
import dev.raspberrykan.cveboard.models.dto.requests.UserLoginRequest;
import dev.raspberrykan.cveboard.models.dto.requests.UserRegisterRequest;
import dev.raspberrykan.cveboard.models.dto.responses.ApiResponse;
import dev.raspberrykan.cveboard.models.dto.responses.UserResponse;
import dev.raspberrykan.cveboard.security.AuthenticatedUser;
import dev.raspberrykan.cveboard.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public UserController(UserService userService, AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserRegisterRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(userService.createUser(request.userName(), request.nickName(), request.password())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred while registering user.", e);
            return ResponseEntity.internalServerError().body(ApiResponse.fail("Register failed."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody UserLoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, servletRequest, servletResponse);

            AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
            if(principal == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.fail("Login failed: Principal not found."));            }
            return ResponseEntity.ok(ApiResponse.ok(userService.recordLogin(principal.getId())));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("Login failed: user not found or password incorrect."));
        } catch (Exception e) {
            log.error("Unexpected error occurred in UserController.", e);
            return ResponseEntity.internalServerError().body(ApiResponse.fail("Login failed."));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateInfo(@RequestBody UpdateUserInfoRequest request, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        try {
            return ResponseEntity.ok(ApiResponse.ok(userService.updateInfo(userId, request.nickName())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail("Please login first."));
        }
        try {
            userService.changePassword(userId, request.oldPassword(), request.newPassword());
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return user.getId();
        }
        return null;
    }
}
