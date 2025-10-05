package nasa.keyvault.service.services;

import java.util.UUID;

public interface OtpService<T> {
    /**
     * Sends an OTP and caches the object.
     * @param path the path to send the OTP to.
     * @param object the object to associate with the OTP.
     * @param userId the user that is being verified. (can be null)
     */
    void sendOtp(String path, T object, UUID userId);

    /**
     * Verifies an OTP, if verified returns the object associated with that OTP, else returns null.
     * @param pass the OTP to verify.
     * @param userId the user that is being verified. (can be null)
     * @return if the OTP is verified then returns the object associated with it, else returns null.
     */
    T verifyOtp(String pass, UUID userId);
}
