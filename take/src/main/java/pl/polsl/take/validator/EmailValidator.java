package pl.polsl.take.validator;

public class EmailValidator {

    public static void validate(String email) {
        if (email == null || !email.contains("@") || email.length() < 6) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

}
