package nasa.keyvault.service.data;

import nasa.keyvault.service.models.Secret;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SecretsRepository extends CrudRepository<Secret, UUID> {
    Page<Secret> findAll(Pageable pageable);
}
