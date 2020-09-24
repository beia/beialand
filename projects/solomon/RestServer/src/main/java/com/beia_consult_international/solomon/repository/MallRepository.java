package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Mall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MallRepository extends JpaRepository<Mall, Long> {
    @Query("select m from Mall m where m.user.id = :userId")
    List<Mall> findAllByUserId(@Param("userId") long id);
}
