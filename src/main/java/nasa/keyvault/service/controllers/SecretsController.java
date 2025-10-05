package nasa.keyvault.service.controllers;

import nasa.keyvault.service.contracts.SecretRequest;
import nasa.keyvault.service.contracts.SecretResponse;
import nasa.keyvault.service.data.SecretsRepository;
import nasa.keyvault.service.models.Secret;
import nasa.keyvault.service.services.EncryptionService;
import nasa.keyvault.service.services.OtpService;
import nasa.keyvault.shared.logging.Logging;
import nasa.keyvault.shared.util.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/vault/secrets")
public class SecretsController {
    private static final Logger logger = LoggerFactory.getLogger(SecretsController.class);
    private static final int Limit = 100;

    private final EncryptionService encryptionService;
    private final OtpService<UUID> otpService;
    private final SecretsRepository repository;

    public SecretsController(EncryptionService encryptionService, OtpService<UUID> otpService, SecretsRepository repository) {
        this.encryptionService = encryptionService;
        this.otpService = otpService;
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<SecretResponse>> getSecrets(@RequestParam(defaultValue = "0") int page) {
        logger.trace("GET /api/vault/secrets");
        Logging.attachDetails("page", page);

        var pageable = PageRequest.of(page, Limit, Sort.by("createdAt").descending());
        var response = repository.findAll(pageable)
                .stream()
                .map(s -> new SecretResponse(s, null))
                .toList();

        Logging.attachDetails("count", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretResponse> getSecret(@PathVariable UUID id) {
        logger.trace("GET /api/vault/secrets/{}", id.toString());
        Logging.attachDetails("secretId", id);

        var secret = repository.findById(id).get();

        if (secret.getWebhookForOtp() != null) {
            logger.info("Requested a secret which has MFA enabled");
            Logging.attachDetails("mfaEnabled", true);

            var user = HttpContext.getUser();
            otpService.sendOtp(secret.getWebhookForOtp(), secret.getId(), user.getId());

            return ResponseEntity.ok(new SecretResponse(secret, null));
        }

        return ResponseEntity.ok(new SecretResponse(secret, decryptionHelper(secret)));
    }

    @GetMapping("/otp/{pass}")
    public ResponseEntity<SecretResponse> getSecretOtp(@PathVariable String pass) {
        logger.trace("GET /api/vault/secrets/otp/{}", pass);

        var user = HttpContext.getUser();
        var secretId = otpService.verifyOtp(pass, user.getId());
        if (secretId == null) {
            throw new IllegalArgumentException("The provided one-time pass is either invalid or expired.");
        }

        var secret = repository.findById(secretId).get();

        return ResponseEntity.ok(new SecretResponse(secret, decryptionHelper(secret)));
    }

    @PostMapping
    public ResponseEntity<SecretResponse> createSecret(@RequestBody SecretRequest request) {
        logger.trace("POST /api/vault/secrets");

        var user = HttpContext.getUser();
        var encryptedSecret = encryptionHelper(request.secret());
        var secret = new Secret(request.title(), request.description(), request.webhookForOtp(), encryptedSecret, user.getId());
        secret = repository.save(secret);

        Logging.attachDetails("secretId", secret.getId());

        return ResponseEntity.ok(new SecretResponse(secret, null));
    }

    @PatchMapping("{id}")
    public ResponseEntity<SecretResponse> updateSecret(@PathVariable UUID id, @RequestBody SecretRequest request) {
        logger.trace("PATCH /api/vault/secrets/{}", id.toString());
        Logging.attachDetails("secretId", id);

        var user = HttpContext.getUser();
        var secret = repository.findById(id).get();

        if (request.title() != null) {
            secret.setTitle(request.title());
        }

        if (request.description() != null) {
            secret.setDescription(request.description());
        }

        if (request.webhookForOtp() != null) {
            secret.setWebhookForOtp(request.webhookForOtp());
        }

        if (request.secret() != null) {
            var encryptedSecret = encryptionHelper(request.secret());
            secret.setEncryptedSecret(encryptedSecret);
        }

        secret.setUpdatedAt(new Date());
        secret.setUpdatedBy(user.getId());

        return ResponseEntity.ok(new SecretResponse(secret, null));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<SecretResponse> deleteSecret(@PathVariable UUID id) {
        logger.trace("DELETE /api/vault/secrets/{}", id.toString());
        Logging.attachDetails("secretId", id);

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private String encryptionHelper(String plain) {
        var encryptedSecret = encryptionService.encrypt(plain);
        if (encryptedSecret == null) {
            logger.error("Secret encryption failed");

            throw new RuntimeException("Something went wrong while saving secret.");
        }

        return encryptedSecret;
    }

    private String decryptionHelper(Secret secret) {
        var secretDecrypted = encryptionService.decrypt(secret.getEncryptedSecret());
        if (secretDecrypted == null) {
            logger.error("Secret decryption failed, request for secret: {}", secret.getId());

            throw new RuntimeException("Something went wrong during secret retrieval.");
        }

        return secretDecrypted;
    }
}
