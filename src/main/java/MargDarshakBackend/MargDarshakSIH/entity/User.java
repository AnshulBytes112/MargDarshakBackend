package MargDarshakBackend.MargDarshakSIH.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public String getId() {
        return id;
    }

    @Id
    private String id;

    private String name;









    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @NonNull String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }



    public void setName(String name) {
        this.name = name;
    }






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
