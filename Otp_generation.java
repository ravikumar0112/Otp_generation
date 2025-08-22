import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import java.security.SecureRandom;
import java.util.Scanner;

public class Otp_generation {

    // Twilio credentials from your Twilio console
    public static final String ACCOUNT_SID = "   twillo account";
    public static final String AUTH_TOKEN = " twilio token";
    public static final String TWILIO_PHONE_NUMBER = " twillo number"; // Your Twilio number

    // OTP storage
    private static String currentOtp;
    private static long expiryTimeMillis;

    // Method to generate OTP with expiry
    public static String generateOtp(int length, int expirySeconds) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }

        currentOtp = otp.toString();
        expiryTimeMillis = System.currentTimeMillis() + (expirySeconds * 1000L);

        return currentOtp;
    }

    // 🔒 Method to mask phone number
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 6) {
            return phoneNumber; // too short to mask
        }
        int visibleDigits = 4; // keep last 4 digits visible
        String countryCode = phoneNumber.substring(0, 3); // e.g. +91
        return countryCode + "******" + phoneNumber.substring(phoneNumber.length() - visibleDigits);
    }

    // Method to send SMS
    public static void sendOtpSms(String toPhoneNumber, String otp) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
                "Your OTP is: " + otp + " (valid for 2 minutes)"
        ).create();

        // ✅ Mask phone number in logs
        System.out.println("OTP sent to " + maskPhoneNumber(toPhoneNumber) + ": " + message.getSid());
    }

    // Method to verify OTP
    public static boolean verifyOtp(String enteredOtp) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > expiryTimeMillis) {
            System.out.println("OTP has expired.");
            return false;
        }
        return currentOtp != null && currentOtp.equals(enteredOtp);
    }

    public static void main(String[] args) {
        // Generate OTP valid for 2 minutes
        String otp = generateOtp(6, 120);
        String recipientPhone = "+91xxxxxxxxx"; // Change to actual phone number

        sendOtpSms(recipientPhone, otp);

        // Ask user to enter OTP
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the OTP you received: ");
        String enteredOtp = scanner.nextLine();

        if (verifyOtp(enteredOtp)) {
            System.out.println("OTP verified successfully!");
        } else {
            System.out.println("OTP verification failed!");
        }
        scanner.close();
    }
}
