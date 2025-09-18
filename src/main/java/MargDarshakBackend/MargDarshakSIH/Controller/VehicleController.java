package MargDarshakBackend.MargDarshakSIH.Controller;

import MargDarshakBackend.MargDarshakSIH.Repository.UserRepository;
import MargDarshakBackend.MargDarshakSIH.Repository.VehicleRepository;
import MargDarshakBackend.MargDarshakSIH.entity.User;
import MargDarshakBackend.MargDarshakSIH.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicle")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getVehicle(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Vehicle> vehicle = vehicleRepository.findByUserId(user.getId());
        return vehicle.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> upsertVehicle(Authentication authentication,
                                           @RequestBody Vehicle incoming) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Vehicle vehicle = vehicleRepository.findByUserId(user.getId())
                .orElseGet(Vehicle::new);
        vehicle.setUserId(user.getId());
        vehicle.setVehicleNumber(incoming.getVehicleNumber());
        vehicle.setModel(incoming.getModel());
        vehicle.setSeatingCapacity(incoming.getSeatingCapacity());
        vehicle.setFuelType(incoming.getFuelType());

        Vehicle saved = vehicleRepository.save(vehicle);
        return ResponseEntity.ok(Map.of(
                "message", "Vehicle saved",
                "vehicle", saved
        ));
    }
}


