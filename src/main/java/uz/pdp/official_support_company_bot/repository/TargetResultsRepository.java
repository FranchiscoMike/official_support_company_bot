package uz.pdp.official_support_company_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.official_support_company_bot.entity.TargetResults;

public interface TargetResultsRepository extends JpaRepository<TargetResults, Long> {
}