package uz.pdp.official_support_company_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.official_support_company_bot.entity.Messages;

public interface PinMessagesRepository extends JpaRepository<Messages, Long> {
}