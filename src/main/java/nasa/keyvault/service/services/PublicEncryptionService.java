package nasa.keyvault.service.services;

/**
 * In the future we will send the secret encrypted with the user's public key.
 * The user will decrypt the key in their client.
 */
public interface PublicEncryptionService<T> {
    /**
     * Encrypts the message using one-way ticket for public encryption.
     * @param key the key to encrypt with (public key).
     * @param message the message to encrypt.
     * @return the encrypted message.
     */
    String encrypt(T key, String message);
}
