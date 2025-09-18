package MargDarshakBackend.MargDarshakSIH.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String phone;
    private String address;
    private String profileImageUrl;
}
