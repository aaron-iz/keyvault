package nasa.keyvault.shared.logging;

import org.slf4j.MDC;

public class Logging {
    public static void attachDetails(String key, String value) {
        var details = MDC.get(LoggingConstants.DetailsKey);
        if (details == null) {
           details = "";
        }

        details += "\n%s: %s".formatted(key, value);
        MDC.put(LoggingConstants.DetailsKey, details);
    }
}
