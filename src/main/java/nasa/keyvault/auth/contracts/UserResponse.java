package nasa.keyvault.auth.contracts;

import nasa.keyvault.auth.models.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record UserResponse(UUID id, String username, Date createdAt, Date updatedAt, List<String> roles) {
    private static List<String> mapRoles(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public UserResponse(User user) {
        this(user.getId(), user.getUsername(), user.getCreatedAt(), user.getUpdatedAt(), mapRoles(user));
    }
}
