package com.personaldatastore.application.service;

import com.personaldatastore.application.domain.UserAccount;
import com.personaldatastore.application.dto.AuthRequest;
import com.personaldatastore.application.dto.AuthResponse;
import com.personaldatastore.application.repository.UserAccountRepository;
import com.personaldatastore.application.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userAccountRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(AuthRequest request) {
        Optional<UserAccount> account = userAccountRepository.findByEmail(request.email());
        if (account.isEmpty() || !passwordEncoder.matches(request.password(), account.get().getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtService.generateToken(account.get().getEmail());
        return new AuthResponse(token, account.get().getEmail(), account.get().getRole());
    }

    public void createDefaultUserIfAbsent() {
        if (userAccountRepository.findByEmail("demo@pds.local").isEmpty()) {
            UserAccount user = new UserAccount(
                    "demo@pds.local",
                    passwordEncoder.encode("password123"),
                    "USER"
            );
            userAccountRepository.save(user);
        }
    }
}
