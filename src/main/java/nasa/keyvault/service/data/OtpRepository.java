package nasa.keyvault.service.data;

import jakarta.transaction.Transactional;
import nasa.keyvault.service.models.OneTimePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OtpRepository extends JpaRepository<OneTimePass, String> {
    @Modifying
    @Transactional
    Long deleteAllByExpirationDateBefore(Date now);
}
