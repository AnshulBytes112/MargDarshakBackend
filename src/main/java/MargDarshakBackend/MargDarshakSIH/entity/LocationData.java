package MargDarshakBackend.MargDarshakSIH.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class LocationData {
    private String driverId;
    private double latitude;
    private double longitude;
    private String timestamp;
}
