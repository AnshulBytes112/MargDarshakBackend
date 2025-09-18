package MargDarshakBackend.MargDarshakSIH.Repository;

import MargDarshakBackend.MargDarshakSIH.entity.DriverDocuments;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DriverDocumentsRepository extends MongoRepository<DriverDocuments, String> {
    Optional<DriverDocuments> findByUserId(String userId);
}


