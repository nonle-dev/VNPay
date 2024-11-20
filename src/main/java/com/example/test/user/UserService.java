package com.example.test.user;
import com.example.test.user.CustomUserDetails;
import com.example.test.user.User;
import com.example.test.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }
    public UserDetails loadUserById(Long id) {
        // Kiểm tra xem user có tồn tại với id trong database không?
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
        return new CustomUserDetails(user);
    }

}
