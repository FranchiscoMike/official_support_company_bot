package uz.pdp.official_support_company_bot.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.pdp.official_support_company_bot.bot.State;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class BotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;

    private String state = State.START;

    private String email;

    private String fullName;

    private String phoneNumber;

    private String position;

    @CreationTimestamp
    private Timestamp createdAt;

    private String weekly_target ;

    private String role;

}
