package nu.senior_project.dormitory_marketplace.service;

import lombok.Getter;
import nu.senior_project.dormitory_marketplace.entity.Role;
import nu.senior_project.dormitory_marketplace.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsI implements UserDetails {

    @Getter
    private final Long id;
    @Getter
    private final String email;
    private final String username;
    private final String password;
    private final List<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());    }

    public UserDetailsI(Long id, String email, String username, String password, List<Role> roles) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public static UserDetailsI build(User user) {
        return new UserDetailsI(user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), user.getRoles());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}