package pe.edu.vallegrande.api.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.api.model.DniModel;

public interface DniRepository extends ReactiveCrudRepository<DniModel, Long> {
    Flux<DniModel> findByStatus(String status);
}
