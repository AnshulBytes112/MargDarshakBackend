package MargDarshakBackend.MargDarshakSIH.Schedule;

import MargDarshakBackend.MargDarshakSIH.entity.TripSchedule;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleLoader {

    private final Map<String, List<TripSchedule>> schedules = new HashMap<>();

    public ScheduleLoader() {
        loadSchedules();
    }

    private void loadSchedules() {
        String csvFile = "src/main/resources/schedules.csv";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length != 7) {
                    System.err.println("Invalid CSV line: " + line);
                    continue;
                }
                TripSchedule schedule = new TripSchedule(
                        data[0].trim(), // tripId
                        data[1].trim(), // vehicleNumber
                        data[2].trim(), // startTime (String)
                        data[3].trim(), // endTime (String)
                        data[4].trim(), // origin
                        data[5].trim(), // destination
                        data[6].trim()  // route
                );
                schedules.computeIfAbsent(data[1].trim(), k -> new ArrayList<>()).add(schedule);
            }
        } catch (Exception e) {
            System.err.println("Error loading schedules: " + e.getMessage());
        }
    }

    public TripSchedule getScheduleByVehicle(String vehicleNumber) {
        List<TripSchedule> vehicleSchedules = schedules.get(vehicleNumber);
        if (vehicleSchedules == null || vehicleSchedules.isEmpty()) {
            return null;
        }
        // If multiple schedules, select the one closest to the current time
        LocalDateTime now = LocalDateTime.now();
        return vehicleSchedules.stream()
                .filter(schedule -> {
                    LocalDateTime startTime = schedule.getStartTime();
                    return !startTime.isAfter(now.plusHours(24)); // Within 24 hours
                })
                .min((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                .orElse(vehicleSchedules.get(0)); // Fallback to first schedule
    }

    public Map<String, List<TripSchedule>> getSchedules() {
        return schedules;
    }
}