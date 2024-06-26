package nu.senior_project.dormitory_marketplace.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nu.senior_project.dormitory_marketplace.dto.auth.AuthenticationRequest;
import nu.senior_project.dormitory_marketplace.dto.auth.AuthenticationResult;
import nu.senior_project.dormitory_marketplace.dto.registration.RegistrationRequest;
import nu.senior_project.dormitory_marketplace.dto.registration.RegistrationResult;
import nu.senior_project.dormitory_marketplace.dto.registration.StoreRegistrationRequest;
import nu.senior_project.dormitory_marketplace.entity.Store;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.exception.bad_request.UnsuccessfulRegistrationException;
import nu.senior_project.dormitory_marketplace.repository.StoreRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final EntityConverterService entityConverterService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceI userDetailsService;
    private final JwtService jwtService;

    public static final Long CUSTOMER_ROLE_ID = 1L;
    public static final Long STORE_ROLE_ID = 2L;

    public static final Long SUPERADMIN_ROLE_ID = 3L;

    public ResponseEntity<RegistrationResult> registerCustomer(RegistrationRequest registrationRequest) {
        RegistrationResult result = new RegistrationResult();

        registerUser(registrationRequest, CUSTOMER_ROLE_ID);

        result.setMessage("Registration successful");
        result.setSuccess(true);

        return ResponseEntity.status(200).body(result);
    }


    // TODO handle exception and return 500. Does not return 500 for now.
    @Transactional
    public ResponseEntity<RegistrationResult> registerStore(RegistrationRequest registrationRequest) {
        RegistrationResult result = new RegistrationResult();

        Long userId = registerUser(registrationRequest, STORE_ROLE_ID);
        createStore((StoreRegistrationRequest) registrationRequest, userId);

        result.setMessage("Registration successful");
        result.setSuccess(true);

        return ResponseEntity.status(200).body(result);
    }

    private void createStore(StoreRegistrationRequest registrationRequest, Long userId) {
        Store store = entityConverterService.toStore(registrationRequest, userId);
        storeRepository.save(store);
    }

    @SneakyThrows
    private Long registerUser(RegistrationRequest registrationRequest, Long roleId) {

        if (isEmailTaken(registrationRequest.getEmail()))
            throw new UnsuccessfulRegistrationException("Email is associated with another account.");

        if (isUsernameTaken(registrationRequest.getUsername()))
            throw new UnsuccessfulRegistrationException("Username is associated with another account. Try another one.");

        if (!isRegistrationAllowed(registrationRequest.getToken(), registrationRequest.getEmail()))
            throw new UnsuccessfulRegistrationException("Registration is not allowed for the email. Please reconfirm the email address.");

        try {
            return saveUser(registrationRequest, roleId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private boolean isUsernameTaken(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    private boolean isRegistrationAllowed(String token, String email) {
        String key = email + "|" + token + "|allowance";
        String allowance = redisService.getValue(key);
        return allowance != null;
    }

    private boolean isEmailTaken(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    private Long saveUser(RegistrationRequest registrationRequest, Long roleId) {
        User user = entityConverterService.toUser(registrationRequest, roleId);
        return userRepository.save(user).getId();
    }

    @SneakyThrows
    public ResponseEntity<AuthenticationResult> authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        AuthenticationResult authenticationResult = new AuthenticationResult();

        if (userDetails != null) {
            authenticationResult.setToken(jwtService.generateJwtToken(userDetails));
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(authenticationResult);
        }

        authenticationResult.setMessage("Invalid credentials.");
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(authenticationResult);
    }
}
