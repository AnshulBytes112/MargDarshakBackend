package MargDarshakBackend.MargDarshakSIH.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    @NonNull
    private String email;

    private String password; // null for Google users
    private String phone;
    private String provider; // "local" or "google"

    // 👇 New fields for Profile Setup
    private String address;
    private String profileImageUrl;

    // Profile completion flag
    private Boolean profileComplete;
}
