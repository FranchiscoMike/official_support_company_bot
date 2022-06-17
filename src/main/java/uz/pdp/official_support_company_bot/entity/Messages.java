package uz.pdp.official_support_company_bot.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.pdp.official_support_company_bot.entity.enums.MessageType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "all_messages")
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BotUser sender;

     @ManyToOne
    private BotUser receiver;

     @Enumerated(EnumType.STRING)
    private MessageType type; // type is crucial

    @CreationTimestamp
    private Timestamp createdAt;

    private String text;


//    public static void main(String[] args) {
//        System.out.println("Current Time is -> "+ LocalDate.now()+" "+ (LocalTime.now()+"").substring(0,8));
//    }

}
