package nasa.keyvault.service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "otps")
public class OneTimePass {
    public static final int PassLength = 6;

    @Id
    @Column(nullable = false, unique = true)
    private String pass;

    @Column(nullable = false)
    private String associationKey;

    @Column(nullable = false)
    private Date expirationDate;

    @Column(nullable = false)
    private UUID userId;

    public OneTimePass(String pass, String associationKey, Date expirationDate, UUID userId) {
        if(pass == null || associationKey == null || expirationDate == null) {
            throw new NullPointerException();
        }

        this.pass = pass;
        this.associationKey = associationKey;
        this.expirationDate = expirationDate;
        this.userId = userId;
    }

    public OneTimePass() { /*JPA*/ }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAssociationKey() {
        return associationKey;
    }

    public void setAssociationKey(String associationKey) {
        this.associationKey = associationKey;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
