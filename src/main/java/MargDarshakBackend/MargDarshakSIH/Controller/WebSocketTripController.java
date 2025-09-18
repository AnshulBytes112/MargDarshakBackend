package MargDarshakBackend.MargDarshakSIH.Controller;


import MargDarshakBackend.MargDarshakSIH.Model.LocationUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import redis.clients.jedis.JedisPooled;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class WebSocketTripController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketTripController.class);

    @Autowired
    private JedisPooled jedis;

    @MessageMapping("/update-location")
    @SendTo("/topic/location-updates")
    public LocationUpdateRequest handleLocationUpdate(LocationUpdateRequest update) {
        logger.info("Received location update: {}", update);

        // Validate data
        if (update.getVehicleNumber() == null || update.getVehicleNumber().isEmpty()) {
            logger.error("Invalid vehicleNumber: {}", update.getVehicleNumber());
            return null; // Or send error via WebSocket
        }
        if (update.getLatitude()==null  || update.getLongitude() ==null) {
            logger.error("Invalid coordinates: lat={}, long={}", update.getLatitude(), update.getLongitude());
            return null;
        }

        // Store in Redis
        String key = "location:" + update.getVehicleNumber();
        jedis.hset(key, "vehicleNumber", update.getVehicleNumber());
        jedis.hset(key, "latitude", update.getLatitude().toString());
        jedis.hset(key, "longitude", update.getLongitude().toString());
        jedis.hset(key, "timestamp", LocalDateTime.now().toString());

        // Broadcast to subscribers (user app)
        return update;
    }
}
