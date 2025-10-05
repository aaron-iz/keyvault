package nasa.keyvault.auth.controller;

import nasa.keyvault.auth.contracts.AuthResponse;
import nasa.keyvault.auth.contracts.LoginRequest;
import nasa.keyvault.auth.contracts.RegisterRequest;
import nasa.keyvault.auth.contracts.UserResponse;
import nasa.keyvault.auth.data.UserRepository;
import nasa.keyvault.auth.models.User;
import nasa.keyvault.auth.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserRepository repository;

    public AuthController(JwtService jwtService, PasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> Login(@RequestBody LoginRequest request) {
        logger.trace("POST /api/auth/login");

        var user = repository.findByUsername(request.username()).get();

        if (!encoder.matches(request.password(), user.getPassword())) {
            logger.warn("User {} had an unsuccessful login attempt", user.getUsername());

            return ResponseEntity.badRequest().build();
        }

        logger.info("logging {} in", user.getUsername());
        var token = jwtService.generateToke(user);

        return ResponseEntity.ok(new AuthResponse(token, null));
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()") // Only users in can create more users
    public ResponseEntity<AuthResponse> Register(@RequestBody RegisterRequest request) {
        logger.trace("POST /api/auth/register");

        var userOptional = repository.findByUsername(request.username());
        if (userOptional.isPresent()) {
            logger.info("Tried to make a user with a used username.");

            throw new IllegalArgumentException("User already in use");
        }

        if (!validatePassword(request.password())) {
            throw new IllegalArgumentException("Password must be 8 characters long, contain at least one of each: capital letter, lowercase letter and a number.");
        }

        logger.info("Creating new user...");

        var user = new User(request.username(), encoder.encode(request.password()));
        user = repository.save(user);

        logger.info("User successfully created, logging them in.");

        var token = jwtService.generateToke(user);

        return ResponseEntity.ok(new AuthResponse(token, new UserResponse(user)));
    }

    private static boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        var capital = false;
        var number = false;
        var lower = false;

        for (char c : password.toCharArray()) {
            capital = capital || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c) != -1;
            lower = lower || "abcdefghijklmnopqrstuvwxyz".indexOf(c) != -1;
            number = number || "1234567890".indexOf(c) != -1;
        }

        return capital && number && lower;
    }
}
