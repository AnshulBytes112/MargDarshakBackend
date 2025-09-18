package MargDarshakBackend.MargDarshakSIH.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "driver_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocuments {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String drivingLicenseUrl;
    private String vehicleRegistrationUrl;
    private String insuranceCertificateUrl;
}


