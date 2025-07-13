package f21.my.id.panel_hosting_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import f21.my.id.panel_hosting_backend.model.User;
import f21.my.id.panel_hosting_backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public User register(String username, String password) {
        if (userRepo.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists");
        }

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashed);

        User saved = userRepo.save(user);
        System.out.println("✅ User saved: " + saved.getUsername());
        return saved;
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    public boolean validateUser(String username, String password) {
        User user = findByUsername(username);
        if (user == null)
            return false;
        return checkPassword(password, user.getPassword());
    }
}
