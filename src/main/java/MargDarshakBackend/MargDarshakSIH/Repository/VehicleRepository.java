package MargDarshakBackend.MargDarshakSIH.Repository;

import MargDarshakBackend.MargDarshakSIH.entity.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    Optional<Vehicle> findByUserId(String userId);
}


