package MargDarshakBackend.MargDarshakSIH.websocket;

import MargDarshakBackend.MargDarshakSIH.Schedule.ScheduleLoader;
import MargDarshakBackend.MargDarshakSIH.entity.TripSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import redis.clients.jedis.JedisPooled;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ScheduleLoader scheduleLoader;

    @Autowired
    private JedisPooled jedis;

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = session.getUri().getQuery().split("token=")[1];
        sessions.put(token, session);
        System.out.println("WebSocket connected for token: " + token);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> locationData = objectMapper.readValue(message.getPayload(), Map.class);
            TripSchedule schedule = createTripScheduleFromMessage(locationData);

            if (schedule == null) {
                session.sendMessage(new TextMessage("{\"error\": \"Invalid location data or no schedule found\"}"));
                return;
            }

            // Store location in Redis
            Map<String, String> locationEntry = new HashMap<>();
            locationEntry.put("latitude", String.valueOf(locationData.get("latitude")));
            locationEntry.put("longitude", String.valueOf(locationData.get("longitude")));
            locationEntry.put("timestamp", (String) locationData.get("timestamp"));
            jedis.hset("trip_location:" + schedule.getTripId(), locationEntry);

            // Broadcast to all sessions
            String broadcastMessage = objectMapper.writeValueAsString(locationEntry);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(broadcastMessage));
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            session.sendMessage(new TextMessage("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    private TripSchedule createTripScheduleFromMessage(Map<String, Object> locationData) {
        try {
            String vehicleNumber = (String) locationData.get("vehicleNumber");
            if (vehicleNumber == null || vehicleNumber.isEmpty()) {
                System.err.println("Missing vehicleNumber in location data");
                return null;
            }

            TripSchedule schedule = scheduleLoader.getScheduleByVehicle(vehicleNumber);
            if (schedule == null) {
                System.err.println("No schedule found for vehicle: " + vehicleNumber);
                return null;
            }
            return schedule;
        } catch (Exception e) {
            System.err.println("Error creating TripSchedule: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String token = session.getUri().getQuery().split("token=")[1];
        sessions.remove(token);
        System.out.println("WebSocket disconnected for token: " + token);
    }
}