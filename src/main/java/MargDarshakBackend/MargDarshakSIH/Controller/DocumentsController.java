package MargDarshakBackend.MargDarshakSIH.Controller;

import MargDarshakBackend.MargDarshakSIH.Repository.DriverDocumentsRepository;
import MargDarshakBackend.MargDarshakSIH.Repository.UserRepository;
import MargDarshakBackend.MargDarshakSIH.entity.DriverDocuments;
import MargDarshakBackend.MargDarshakSIH.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentsController {

    @Autowired
    private DriverDocumentsRepository documentsRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getDocuments(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.status(401).build();
        Optional<DriverDocuments> docs = documentsRepository.findByUserId(user.getId());
        return docs.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(Authentication authentication,
                                            @RequestParam("document") MultipartFile file,
                                            @RequestParam("documentType") String documentType) throws IOException {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.status(401).build();

        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "doc";
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID() + ext;

        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);
        Path destination = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();

        DriverDocuments docs = documentsRepository.findByUserId(user.getId())
                .orElseGet(DriverDocuments::new);
        docs.setUserId(user.getId());
        switch (documentType) {
            case "drivingLicense" -> docs.setDrivingLicenseUrl(fileUrl);
            case "vehicleRegistration" -> docs.setVehicleRegistrationUrl(fileUrl);
            case "insuranceCertificate" -> docs.setInsuranceCertificateUrl(fileUrl);
            default -> {
                return ResponseEntity.badRequest().body("Unknown documentType");
            }
        }
        documentsRepository.save(docs);

        return ResponseEntity.ok(Map.of(
                "documentUrl", fileUrl,
                "message", "Document uploaded"
        ));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeProfile(Authentication authentication,
                                             @RequestBody Map<String, Object> payload) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.status(401).build();

        user.setProfileComplete(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile completed"));
    }
}


