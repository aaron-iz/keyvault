package nasa.keyvault.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtpConfiguration {
    @Value("${otp.duration-ms}")
    public long DurationMs;

    @Value("${otp.message-template}")
    public String MessageTemplate;
}
