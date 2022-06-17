package uz.pdp.official_support_company_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.official_support_company_bot.entity.BotUser;

import java.util.Optional;

public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    Optional<BotUser> findByChatId(String chatId);
    Optional<BotUser> findByFullName(String fullName);
}