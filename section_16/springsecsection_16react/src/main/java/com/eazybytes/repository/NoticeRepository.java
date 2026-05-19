package com.eazybytes.repository;

import com.eazybytes.model.Notice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Notice repository with custom query.
 * NOTE: R2DBC uses native SQL queries (not JPQL/HQL like JPA).
 * The @Query annotation here is from org.springframework.data.r2dbc.repository.Query.
 */
@Repository
public interface NoticeRepository extends ReactiveCrudRepository<Notice, Long> {

    @Query("SELECT * FROM notice_details n WHERE CURRENT_DATE BETWEEN n.notic_beg_dt AND n.notic_end_dt")
    Flux<Notice> findAllActiveNotices();
}
