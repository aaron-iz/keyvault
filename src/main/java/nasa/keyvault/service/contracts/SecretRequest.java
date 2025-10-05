package nasa.keyvault.service.contracts;

// TODO - Service improvement: use RSA to accept secrets and send them securely.
public record SecretRequest(String title, String description, String secret, String webhookForOtp) { }
