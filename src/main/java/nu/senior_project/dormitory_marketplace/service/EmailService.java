package nu.senior_project.dormitory_marketplace.service;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.senior_project.dormitory_marketplace.dto.code.SendCodeRequest;
import nu.senior_project.dormitory_marketplace.dto.code.SendCodeResult;
import nu.senior_project.dormitory_marketplace.dto.code.ValidateCodeRequest;
import nu.senior_project.dormitory_marketplace.dto.code.ValidateCodeResult;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final RedisService redisService;
    private final String EMAIL_VERIFICATION_MESSAGE = "Your verification code is ";
    private final String EMAIL_VERIFICATION_SUBJECT = "Verify your email";
    private final String REGISTRATION_ALLOWED = "REGISTRATION_ALLOWED";

    @Value("${spring.mail.username}")
    private String from;

    public ResponseEntity<SendCodeResult> sendCode(SendCodeRequest sendCodeRequest) {
        Integer code = ThreadLocalRandom.current().nextInt(1000, 10000);
        String recipient = sendCodeRequest.getRecipient();
        SimpleMailMessage message = createRegistrationCodeMessage(recipient, code);
        SendCodeResult sendCodeResult = new SendCodeResult();

        try {
            mailSender.send(message);
            sendCodeResult.setToken(cacheCode(recipient, code));
        } catch (MailException | RedisException e) {
            log.error("Exception in sendCode: ", e);
            sendCodeResult.setMessage("Error has happened during email sending. Please retry later or contact admins");
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(sendCodeResult);
        }

        sendCodeResult.setSuccess(true);
        sendCodeResult.setMessage("Email has been successfully sent");
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(sendCodeResult);
    }


    public ResponseEntity<ValidateCodeResult> validateCode(ValidateCodeRequest validateCodeRequest) {

        String key = validateCodeRequest.getRecipient() + "|" + validateCodeRequest.getToken() + "|code";
        String savedCode = redisService.getValue(key);
        String userCode = validateCodeRequest.getUserCode();
        ValidateCodeResult validateCodeResult = new ValidateCodeResult();

        if (savedCode == null) {
            validateCodeResult.setMessage("Your code has expired. Please, retry requesting it.");
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(validateCodeResult);
        }

        if (!savedCode.equals(userCode)) {
            validateCodeResult.setMessage("Incorrect code.");
            return ResponseEntity.status(400).body(validateCodeResult);
        }

        validateCodeResult.setSuccess(true);
        validateCodeResult.setMessage("Validation successful.");
        allowRegistration(validateCodeRequest.getToken(), validateCodeRequest.getRecipient());
        return ResponseEntity.status(200).body(validateCodeResult);
    }

    private void allowRegistration(String token, String email) {
        String key = email + "|" + token + "|allowance";
        redisService.setValue(key, REGISTRATION_ALLOWED, 2L, TimeUnit.MINUTES);
    }

    private String cacheCode(String recipient, Integer code) {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z').build();
        String token = generator.generate(10);
        String key = recipient + "|" + token + "|code";
        redisService.setValue(key, code.toString(), 2L, TimeUnit.MINUTES);
        return token;
    }

    private SimpleMailMessage createRegistrationCodeMessage(String recipient, Integer code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(EMAIL_VERIFICATION_SUBJECT);
        simpleMailMessage.setText(EMAIL_VERIFICATION_MESSAGE + code);
        simpleMailMessage.setTo(recipient);
        simpleMailMessage.setFrom(from);
        return simpleMailMessage;
    }

}
