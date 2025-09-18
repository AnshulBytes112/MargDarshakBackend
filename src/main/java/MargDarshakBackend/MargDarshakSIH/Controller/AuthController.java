package MargDarshakBackend.MargDarshakSIH.Controller;

import MargDarshakBackend.MargDarshakSIH.Repository.UserRepository;
import MargDarshakBackend.MargDarshakSIH.Utils.JwtUtils;
import MargDarshakBackend.MargDarshakSIH.dto.LoginRequest;
import MargDarshakBackend.MargDarshakSIH.dto.RegisterRequest;
import MargDarshakBackend.MargDarshakSIH.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtil;

    @PostMapping("/auth/register")
    private ResponseEntity<?> signup(@RequestBody RegisterRequest request) {
        try {
            // ✅ Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            }

            // ✅ Check if username already exists
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhone(request.getPhone());
            User savedUser = userRepository.save(user);

            // ✅ Generate JWT
            String token = jwtUtil.generateToken(savedUser.getEmail());

            // ✅ Response with token + user
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", savedUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during registration", e);
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/auth/login")
    private ResponseEntity<?> login(@RequestBody LoginRequest user) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );

            // ✅ Find user by email
            String email = user.getEmail();
            String password = user.getPassword();
            User userr = userRepository.findByEmail(email);
            if (userr == null) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            // ✅ Validate password
            if (!passwordEncoder.matches(user.getPassword(), userr.getPassword())) {
                return new ResponseEntity<>("Invalid credentials", HttpStatus.BAD_REQUEST);
            }

            // ✅ Generate JWT
            String token = jwtUtil.generateToken(userr.getEmail());

            // ✅ Response with token + user
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userr);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during login", e);
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<Map<String, String>> deleteUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Account deleted successfully");

        return ResponseEntity.ok(response);
    }


}
