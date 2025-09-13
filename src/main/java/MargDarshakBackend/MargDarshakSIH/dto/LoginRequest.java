package MargDarshakBackend.MargDarshakSIH.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password; // null for Google users
}
