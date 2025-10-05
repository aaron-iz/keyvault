package nasa.keyvault.shared.logging;

import org.slf4j.MDC;

public class Logging {
    public static void attachDetails(String key, Object value) {
        var details = MDC.get(LoggingConstants.DetailsKey);
        if (details == null) {
           details = "";
        }

        details += "\n%s: %s".formatted(key, value.toString());
        MDC.put(LoggingConstants.DetailsKey, details);
    }
}
