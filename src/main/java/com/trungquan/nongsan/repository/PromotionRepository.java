package com.trungquan.nongsan.repository;

import com.trungquan.nongsan.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByActiveFlag(boolean activeFlag);

    List<Promotion> findAllByActiveFlag(boolean activeFlag);

    List<Promotion> findAllByActiveFlagOrderByIdDesc(boolean activeFlag);


    Optional<Promotion> findByTitle(String name);

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE " +
            "ORDER BY ABS(DATEDIFF(CURRENT_DATE, p.startDate)) ASC, p.id ASC")
    Promotion findClosestActivePromotion();
}
