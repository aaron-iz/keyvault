package nasa.keyvault.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyVaultConfiguration {
    @Value("${vault.master}")
    private String masterKey;

    public String getMasterKey() {
        return masterKey;
    }
}
