package MargDarshakBackend.MargDarshakSIH.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdateRequest {
    @JsonProperty("vehicle_number")
    private String vehicleNumber;

    public Double getLatitude() {
        return latitude;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    private String timestamp;
    @Override
    public String toString() {
        return "LocationUpdate{" +
                "vehicleNumber='" + vehicleNumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

