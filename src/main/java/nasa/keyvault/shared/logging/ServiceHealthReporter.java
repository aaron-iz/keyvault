package nasa.keyvault.shared.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ServiceHealthReporter {
    private static final int DelayInMinutes = 1;
    private static final long Delay = DelayInMinutes * 60 * 1000;

    private static final Logger logger = LoggerFactory.getLogger(ServiceHealthReporter.class);

    @Scheduled(initialDelay = Delay, fixedRate = Delay)
    private void reportServiceHealth() {
        logger.info("Service is up and running.");
    }
}
