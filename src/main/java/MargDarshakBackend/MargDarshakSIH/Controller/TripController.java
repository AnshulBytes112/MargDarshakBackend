package MargDarshakBackend.MargDarshakSIH.Controller;

import MargDarshakBackend.MargDarshakSIH.Schedule.ScheduleLoader;
import MargDarshakBackend.MargDarshakSIH.entity.TripSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPooled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private ScheduleLoader scheduleLoader;

 @Autowired
    private JedisPooled jedis; // Redis client

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startTrip(@RequestBody TripRequest tripRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            if (tripRequest.getVehicleNumber() == null || tripRequest.getVehicleNumber().isEmpty()) {
                response.put("message", "vehicleNumber is required");
                return ResponseEntity.badRequest().body(response);
            }
            if (tripRequest.getStartTime() == null || tripRequest.getStartTime().isEmpty()) {
                response.put("message", "startTime is required");
                return ResponseEntity.badRequest().body(response);
            }

            TripSchedule schedule = scheduleLoader.getScheduleByVehicle(tripRequest.getVehicleNumber());
            if (schedule == null) {
                response.put("message", "No schedule found for vehicle: " + tripRequest.getVehicleNumber());
                return ResponseEntity.badRequest().body(response);
            }

            // Update startTime
            try {
                schedule.setStartTime(LocalDateTime.parse(tripRequest.getStartTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception e) {
                response.put("message", "Invalid startTime format: " + tripRequest.getStartTime());
                return ResponseEntity.badRequest().body(response);
            }

            // Store in Redis
            Map<String, String> tripData = new HashMap<>();
            tripData.put("tripId", schedule.getTripId());
            tripData.put("vehicleNumber", schedule.getVehicleNumber());
            tripData.put("startTime", schedule.getStartTime().toString());
            tripData.put("endTime", schedule.getEndTime().toString());
            tripData.put("origin", schedule.getOrigin());
            tripData.put("destination", schedule.getDestination());
            tripData.put("route", schedule.getRoute());
            jedis.hset("trip_schedule:" + schedule.getTripId(), tripData); // Single hset call

            response.put("message", "Trip started successfully");
            response.put("tripId", schedule.getTripId());
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("message", "Failed to start trip: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/location/{vehicleNumber}")
    public ResponseEntity<Map<String, String>> getLocation(@PathVariable String vehicleNumber) {
        Map<String, String> response = new HashMap<>();
        Map<String, String> locationData = jedis.hgetAll("location:" + vehicleNumber);
        if (locationData.isEmpty()) {
            response.put("message", "No location data found for vehicle: " + vehicleNumber);
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(locationData);
    }
}
