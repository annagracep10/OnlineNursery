package com.techphantomexample.usermicroservice.services;

import com.techphantomexample.usermicroservice.entity.UserEntity;
import com.techphantomexample.usermicroservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class ResetPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final ConcurrentMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private static final long OTP_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(10);

    public UserEntity findByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    public void storeOtpForEmail(String email, String otp) {
        long currentTime = System.currentTimeMillis();
        otpStore.put(email, new OtpEntry(otp, currentTime + OTP_EXPIRATION_TIME));
    }

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom("gracewithlatte@gmail.com");
        mailMessage.setSubject("Your OTP for Password Reset");
        mailMessage.setText("Your OTP for password reset is: " + otp);
        mailSender.send(mailMessage);
    }

    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) {
            return false;
        }

        if (entry.getExpiryTime() < System.currentTimeMillis()) {
            otpStore.remove(email);
            return false;
        }

        if (entry.getOtp().equals(otp)) {
            otpStore.remove(email);
            return true;
        }

        return false;
    }

    public String resetPassword(String email, String newPassword) {
        UserEntity user = findByEmail(email);
        if (user == null) {
            return "User not found.";
        }

        user.setUserPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);
        return "Password reset successfully.";
    }

    private static class OtpEntry {
        private final String otp;
        private final long expiryTime;

        public OtpEntry(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}
