package com.talkeitime.chat.controller;

import com.talkeitime.chat.dto.LoginDTO;
import com.talkeitime.chat.dto.RegisterUserDTO;
import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.service.AuthService;
import com.talkeitime.chat.utils.AuthResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO)
        throws BadRequestException {
        return ResponseEntity.ok().body(authService.registerUser(registerUserDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginDTO loginDTO)
        throws BadRequestException {
        return ResponseEntity.ok().body(authService.loginUser(loginDTO));
    }
}
