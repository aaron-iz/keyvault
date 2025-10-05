package nasa.keyvault.service.services;

import nasa.keyvault.service.config.OtpConfiguration;
import nasa.keyvault.service.data.OtpRepository;
import nasa.keyvault.service.models.OneTimePass;
import nasa.keyvault.shared.external.DiscordWebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class DiscordOtpService implements OtpService<UUID> {
    private static final Logger logger = LoggerFactory.getLogger(DiscordOtpService.class);
    private static final long CleanUpRate = 5 * 60 * 1000;

    private final OtpRepository repository;
    private final RestTemplate restTemplate;
    private final OtpConfiguration configuration;
    private final Random random = new Random();

    public DiscordOtpService(OtpRepository repository, RestTemplate restTemplate, OtpConfiguration configuration) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    @Scheduled(initialDelay = CleanUpRate, fixedRate = CleanUpRate)
    public void cleanupExpiredPasscodes() {
        logger.trace("{}.cleanupExpiredPasscodes", DiscordOtpService.class.getSimpleName());

        var deleted = repository.deleteAllByExpirationDateBefore(new Date());

        logger.info("Deleted {} expired otps", deleted);
    }

    @Override
    public void sendOtp(String path, UUID object, UUID userId) {
        logger.trace("{}.sendOtp", DiscordOtpService.class.getSimpleName());

        var pass = generateOtp();
        while (repository.existsById(pass)) {
            // Should not occur but just to be safe.
            logger.info("Generated pass which already exists.");

            pass = generateOtp();
        }

        var exp = new Date(System.currentTimeMillis() + configuration.DurationMs);
        var otp = new OneTimePass(pass, object.toString(), exp, userId);
        repository.save(otp);

        var message = configuration.MessageTemplate.formatted(pass);
        CompletableFuture.runAsync(() -> sendOtp(path, message));
    }

    @Override
    public UUID verifyOtp(String pass, UUID userId) {
        logger.trace("{}.verifyOtp", DiscordOtpService.class.getSimpleName());

        if (!repository.existsById(pass)) {
            return null;
        }

        var otp = repository.findById(pass).get();
        if (otp.getExpirationDate().before(new Date())) {
            logger.info("Otp verification of an already expired pass, deleting.");

            repository.deleteById(otp.getPass());

            return null;
        }

        if (!otp.getUserId().equals(userId)) {
            logger.warn("User {} attempted to consume an otp which is not theirs.", userId);

            return null;
        }

        // The otp is used up
        repository.deleteById(otp.getPass());

        return UUID.fromString(otp.getAssociationKey());
    }

    private void sendOtp(String path, String pass) {
        try {
            var request = new DiscordWebhookRequest(pass);
            restTemplate.postForEntity(path, request, String.class);
        } catch (Exception ex) {
            logger.error("An exception was thrown while sending an otp", ex);
        }
    }

    private String generateOtp() {
        var otp = "";
        while (otp.length() < OneTimePass.PassLength) {
            otp += random.nextInt(10);
        }

        return otp;
    }
}
