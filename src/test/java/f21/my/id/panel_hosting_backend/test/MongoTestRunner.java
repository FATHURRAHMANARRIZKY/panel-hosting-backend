
// package f21.my.id.panel_hosting_backend.test;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import f21.my.id.panel_hosting_backend.model.User;
// import f21.my.id.panel_hosting_backend.repository.UserRepository;

// @Component
// public class MongoTestRunner implements CommandLineRunner {

//     @Autowired
//     private UserRepository userRepo;

//     @Override
//     public void run(String... args) {
//         System.out.println("➡ Mencoba menyimpan user dummy ke MongoDB...");

//         User u = new User();
//         u.setUsername("dummy");
//         u.setPassword("dummy");

//         userRepo.save(u);
//         System.out.println("✅ User dummy tersimpan.");
//     }
// }