package nasa.keyvault.service.services;

import nasa.keyvault.service.config.KeyVaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class AEXEncryptionServiceTests {

    private AESEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        // Mock KeyVaultConfiguration
        KeyVaultConfiguration config = Mockito.mock(KeyVaultConfiguration.class);
        Mockito.when(config.getMasterKey()).thenReturn("my-strong-master-key");

        encryptionService = new AESEncryptionService(config);
    }

    @Test
    void encryptAndDecrypt_ShouldReturnOriginalText() {
        String original = "mySecretPassword123!";
        String encrypted = encryptionService.encrypt(original);
        assertNotNull(encrypted, "Encrypted string should not be null");

        String decrypted = encryptionService.decrypt(encrypted);
        assertNotNull(decrypted, "Decrypted string should not be null");
        assertEquals(original, decrypted, "Decrypted text should match original");
    }

    @Test
    void decrypt_InvalidCipher_ShouldReturnNull() {
        String invalidCipher = "invalidCipherText";
        String decrypted = encryptionService.decrypt(invalidCipher);
        assertNull(decrypted, "Decrypting invalid cipher should return null");
    }
}
