package com.example.test;

import com.example.test.user.User;
import com.example.test.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            User existingUser = userRepository.findByUsername("nonlee");
            if (existingUser == null) {
                User user = new User();
                user.setUsername("nonlee");
                user.setPassword(passwordEncoder.encode("kcms142857"));
                userRepository.save(user);
                System.out.println("User created: " + user);
            } else {
                System.out.println("User 'loda' đã tồn tại.");
            }
        } catch (Exception e) {
            System.err.println("Đã xảy ra lỗi khi khởi tạo user: " + e.getMessage());
        }
    }
}
