package com.talkeitime.chat.controller;

import com.talkeitime.chat.dto.LoginDTO;
import com.talkeitime.chat.dto.RegisterUserDTO;
import com.talkeitime.chat.dto.UserDTO;
import com.talkeitime.chat.service.AuthService;
import com.talkeitime.chat.utils.AuthResponse;
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
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterUserDTO registerUserDTO)
        throws BadRequestException {
        if (
            registerUserDTO != null &&
            registerUserDTO.getUsername() == null &&
            registerUserDTO.getPassword() == null &&
            registerUserDTO.getEmail() == null
        ) {
            throw new BadRequestException("Invalid request");
        }
        UserDTO userDTO = authService.registerUser(registerUserDTO);

        return ResponseEntity.ok().body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginDTO loginDTO)
        throws BadRequestException {
        if (loginDTO == null || loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            throw new BadRequestException("Username or password is required!!!");
        }
        AuthResponse token = authService.loginUser(loginDTO);

        return ResponseEntity.ok().body(token);
    }
}
