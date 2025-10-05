package nasa.keyvault.shared.util;

public class StringLengthGuard {
    public static void maxLength(String value, String fieldName, int maxLength, boolean allowNullOrEmpty) {
        if (!allowNullOrEmpty && (value == null || value.isEmpty())) {
            throw new IllegalArgumentException("%s is null or empty.".formatted(fieldName));
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException("%s is too long, max allowed length is %d.".formatted(fieldName, maxLength));
        }
    }

    public static void maxLength(String value, String fieldName, int maxLength) {
        maxLength(value, fieldName, maxLength, true);
    }
}
