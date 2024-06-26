package nu.senior_project.dormitory_marketplace.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nu.senior_project.dormitory_marketplace.dto.auth.AuthenticationRequest;
import nu.senior_project.dormitory_marketplace.dto.auth.AuthenticationResult;
import nu.senior_project.dormitory_marketplace.dto.code.SendCodeRequest;
import nu.senior_project.dormitory_marketplace.dto.code.SendCodeResult;
import nu.senior_project.dormitory_marketplace.dto.code.ValidateCodeRequest;
import nu.senior_project.dormitory_marketplace.dto.code.ValidateCodeResult;
import nu.senior_project.dormitory_marketplace.dto.registration.RegistrationRequest;
import nu.senior_project.dormitory_marketplace.dto.registration.RegistrationResult;
import nu.senior_project.dormitory_marketplace.dto.registration.StoreRegistrationRequest;
import nu.senior_project.dormitory_marketplace.exception.bad_request.FormValidationException;
import nu.senior_project.dormitory_marketplace.service.AuthService;
import nu.senior_project.dormitory_marketplace.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailService emailService;
    private final AuthService authService;

    @PostMapping("/authenticate")
    @SneakyThrows
    public ResponseEntity<AuthenticationResult> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return authService.authenticate(authenticationRequest);
    }
    @PostMapping("/register/customer")
    @SneakyThrows
    public ResponseEntity<RegistrationResult> registerCustomer(@RequestBody @Valid RegistrationRequest registrationRequest,
                                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new FormValidationException(bindingResult);

        return authService.registerCustomer(registrationRequest);
    }

    @PostMapping("/register/store")
    @SneakyThrows
    public ResponseEntity<RegistrationResult> registerStore(@RequestBody @Valid StoreRegistrationRequest storeRegistrationRequest,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new FormValidationException(bindingResult);

        return authService.registerStore(storeRegistrationRequest);
    }

    @PostMapping("/sendCode")
    @SneakyThrows
    public ResponseEntity<SendCodeResult> sendCode(@RequestBody @Valid SendCodeRequest sendCodeRequest,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new FormValidationException(bindingResult);
        }
        return emailService.sendCode(sendCodeRequest);
    }

    @PostMapping("/validateCode")
    public ResponseEntity<ValidateCodeResult> validateCode(@RequestBody ValidateCodeRequest validateCodeRequest) {
        return emailService.validateCode(validateCodeRequest);
    }
}
