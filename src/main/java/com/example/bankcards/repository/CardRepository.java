package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query(value = """
            SELECT * FROM card
            WHERE (:number IS NULL OR number ILIKE :number)
            AND (CAST(:status AS varchar) IS NULL OR status = CAST(:status AS varchar))
            AND (:userId IS NULL OR user_id = :userId)
            ORDER BY id
            """,
            countQuery = """
            SELECT count(*) FROM card
            WHERE (:number IS NULL OR number ILIKE :number)
            AND (CAST(:status AS varchar) IS NULL OR status = CAST(:status AS varchar))
            AND (:userId IS NULL OR user_id = :userId)
            """,
            nativeQuery = true)
    Page<Card> findByNumberAndStatus(@Param("number") String number,
                                     @Param("status") String status,
                                     @Param("userId" ) UUID userId,
                                     Pageable pageable);
}