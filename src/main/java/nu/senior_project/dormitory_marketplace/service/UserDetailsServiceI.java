package nu.senior_project.dormitory_marketplace.service;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.Role;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceI implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("no account with username:" + username));
        List<Role> roles = user.getRoles();
        return new UserDetailsI(user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), roles);
    }
}