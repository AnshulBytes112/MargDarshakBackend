package MargDarshakBackend.MargDarshakSIH.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TripSchedule {
    private String tripId;
    private String vehicleNumber;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }



    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String origin;
    private String destination;

    public String getRoute() {
        return route;
    }

    public String getDestination() {
        return destination;
    }

    public String getOrigin() {
        return origin;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getTripId() {
        return tripId;
    }

    private String route;

    public TripSchedule(String tripId, String vehicleNumber, String startTime, String endTime,
                        String origin, String destination, String route) {
        this.tripId = tripId;
        this.vehicleNumber = vehicleNumber;
        this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.endTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.origin = origin;
        this.destination = destination;
        this.route = route;
    }

}