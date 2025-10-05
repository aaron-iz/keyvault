package nasa.keyvault.service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import nasa.keyvault.shared.util.StringLengthGuard;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "secrets")
public class Secret {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = SecretFieldsConstants.TitleLength)
    private String title;

    @Column(length = SecretFieldsConstants.DescriptionLength)
    private String description;

    @Column
    private String webhookForOtp;

    @Column(nullable = false, length = 1024)
    private String encryptedSecret;

    @Column(nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    private UUID updatedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @Column
    @UpdateTimestamp
    private Date updatedAt;

    public Secret(String title, String description, String webhookForOtp, String encryptedSecret, UUID createdBy) {
        StringLengthGuard.maxLength(title, "title", SecretFieldsConstants.TitleLength, false);
        StringLengthGuard.maxLength(description, "description", SecretFieldsConstants.DescriptionLength);

        if (webhookForOtp != null && !webhookForOtp.startsWith(SecretFieldsConstants.OtpWebhookAllowedPrefix)) {
            throw new IllegalArgumentException("Webhook for OTP is illegal or not supported.");
        }

        this.title = title;
        this.description = description;
        this.webhookForOtp = webhookForOtp;
        this.encryptedSecret = encryptedSecret;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
    }

    public Secret() { /* JPA */ }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getWebhookForOtp() {
        return webhookForOtp;
    }

    public String getEncryptedSecret() {
        return encryptedSecret;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        StringLengthGuard.maxLength(title, "title", SecretFieldsConstants.TitleLength, false);

        this.title = title;
    }

    public void setDescription(String description) {
        StringLengthGuard.maxLength(description, "description", SecretFieldsConstants.DescriptionLength);

        this.description = description;
    }

    public void setWebhookForOtp(String webhookForOtp) {
        if (webhookForOtp != null && !webhookForOtp.startsWith(SecretFieldsConstants.OtpWebhookAllowedPrefix)) {
            throw new IllegalArgumentException("Webhook for OTP is illegal or not supported.");
        }

        this.webhookForOtp = webhookForOtp;
    }

    public void setEncryptedSecret(String encryptedSecret) {
        this.encryptedSecret = encryptedSecret;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
