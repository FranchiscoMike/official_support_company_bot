package uz.pdp.official_support_company_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.official_support_company_bot.entity.Messages;
import uz.pdp.official_support_company_bot.entity.enums.MessageType;

import java.util.Optional;

public interface MessagesRepository extends JpaRepository<Messages, Long> {
    Optional<Messages> findBySender_IdAndTypeIsNull(Long id);


    Optional<Messages> findBySender_IdAndTypeAndTextIsNull(Long sender_id, MessageType type);
}