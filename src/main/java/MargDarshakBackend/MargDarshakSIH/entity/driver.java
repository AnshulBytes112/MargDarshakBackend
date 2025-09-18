package MargDarshakBackend.MargDarshakSIH.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class driver {

    @Id
    private String id;

    private String name;
    private String vehicleNumber;
    private String phone;
    private String licenseNumber;

    private String status; // active, inactive

}

