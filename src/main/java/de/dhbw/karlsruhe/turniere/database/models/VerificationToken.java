package de.dhbw.karlsruhe.turniere.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION_MINUTES = 60 * 24;
    String token;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User user;
    private Date expiryDate;

    public VerificationToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expiryDate = calculateExpiryDate();
    }

    private Date calculateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
        calendar.add(Calendar.MINUTE, EXPIRATION_MINUTES);
        return calendar.getTime();
    }

    public boolean isExpired() {
        return Calendar.getInstance().getTime().after(this.expiryDate);
    }
}
