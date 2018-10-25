package de.dhbw.karlsruhe.turniere.registration;

import de.dhbw.karlsruhe.turniere.database.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegisterEvent extends ApplicationEvent {
    private User user;
    private String url;

    public OnRegisterEvent(User user, String url) {
        super(user);
        this.user = user;
        this.url = url;
    }
}
