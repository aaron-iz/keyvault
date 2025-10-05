package nasa.keyvault.service.services;

public interface EncryptionService {
    /**
     * Encrypts plain text.
     * @param plain the plain text to encrypt.
     * @return the encrypted cipher text.
     */
    String encrypt(String plain);

    /**
     * Decrypts cipher text.
     * @param cipher the cipher text to decrypt.
     * @return the decrypted plain text.
     */
    String decrypt(String cipher);
}
