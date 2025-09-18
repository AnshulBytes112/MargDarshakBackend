package MargDarshakBackend.MargDarshakSIH.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    // Mock database or service to map email to vehicleNumber
    private final Map<String, String> driverVehicleMap = new HashMap<>();

    public DriverController() {
        // Mock data (replace with actual database query)
        driverVehicleMap.put("driver@example.com", "KA01AB1234");
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getDriverProfile() {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName(); // Get email from JWT

            String vehicleNumber = driverVehicleMap.get(email);
            if (vehicleNumber == null) {
                response.put("message", "No vehicle assigned to driver: " + email);
                return ResponseEntity.badRequest().body(response);
            }

            response.put("vehicleNumber", vehicleNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to fetch profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
