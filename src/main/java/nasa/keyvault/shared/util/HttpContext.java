package nasa.keyvault.shared.util;

import nasa.keyvault.auth.models.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class HttpContext {
    public static User getUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
