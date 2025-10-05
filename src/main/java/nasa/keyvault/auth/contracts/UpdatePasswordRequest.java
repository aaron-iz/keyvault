package nasa.keyvault.auth.contracts;

public record UpdatePasswordRequest(String oldPassword, String newPassword) { }
