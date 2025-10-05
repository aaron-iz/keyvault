package nasa.keyvault.service.contracts;

import nasa.keyvault.service.models.Secret;

import java.util.Date;
import java.util.UUID;

public record SecretResponse(
        UUID id,
        String title,
        String description,
        String secret,
        String webhookForOtp,
        UUID createdBy,
        UUID updatedBy,
        Date createdAt,
        Date updatedAt) {
    public SecretResponse(Secret secretModel, String secret) {
        this(
                secretModel.getId(),
                secretModel.getTitle(),
                secretModel.getDescription(),
                secret,
                secretModel.getWebhookForOtp(),
                secretModel.getCreatedBy(),
                secretModel.getUpdatedBy(),
                secretModel.getCreatedAt(),
                secretModel.getUpdatedAt());
    }
}
