package uz.pdp.official_support_company_bot.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class TargetResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // yani bitta odamda bir nechta
    private BotUser user;

    private String target;

    private String result;
}
