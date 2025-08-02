package gr.aueb.cf.cafeapp.employee_management.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecUtil {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private SecUtil(){

    }

    public static String hashPassword(String inputPassword) {
        return ENCODER.encode(inputPassword);
    }

    public static boolean checkPassword(String inputPassword, String storedHashedPassword) {
        return ENCODER.matches(inputPassword, storedHashedPassword);
    }
}
