package MargDarshakBackend.MargDarshakSIH.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String vehicleNumber;
    private String model;
    private Integer seatingCapacity;
    private String fuelType;
}


