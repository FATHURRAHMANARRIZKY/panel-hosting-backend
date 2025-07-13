package f21.my.id.panel_hosting_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import f21.my.id.panel_hosting_backend.model.User;
import f21.my.id.panel_hosting_backend.security.JwtUtil;
import f21.my.id.panel_hosting_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        try {
            userService.register(username, password);
            return ResponseEntity.ok("Register success");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userService.findByUsername(username);
        if (user == null || !userService.checkPassword(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        ResponseCookie cookie = ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().body("Login success");
    }
}