package MargDarshakBackend.MargDarshakSIH.Controller;

import MargDarshakBackend.MargDarshakSIH.Repository.UserRepository;
import MargDarshakBackend.MargDarshakSIH.dto.ProfileUpdateRequest;
import MargDarshakBackend.MargDarshakSIH.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.Authenticator;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ✅ Get currently authenticated user's profile
    @GetMapping
    public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        String id = userRepository.findByEmail(username).getId();
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Get user profile by ID (to support frontend call /api/user/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestBody ProfileUpdateRequest request
    ) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Update only provided fields
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            user.setAddress(request.getAddress());
        }
        if (request.getProfileImageUrl() != null && !request.getProfileImageUrl().isEmpty()) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // Don’t allow email or password updates here for security
        // (can be handled separately if needed)

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // ✅ Upload profile photo and update user's profileImageUrl
    @PostMapping("/photo")
    public ResponseEntity<?> uploadProfilePhoto(Authentication authentication,
                                                @RequestParam("photo") MultipartFile photo) throws IOException {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (photo.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }

        String original = photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "photo";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;

        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);
        Path destination = uploadDir.resolve(filename);
        Files.copy(photo.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();

        user.setProfileImageUrl(fileUrl);
        userRepository.save(user);

        return ResponseEntity.ok(java.util.Map.of(
                "photoUrl", fileUrl,
                "message", "Profile photo uploaded"
        ));
    }
}
