package org.elastos.dao;

import io.lettuce.core.GeoArgs;
import org.elastos.dto.VoteRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    List<VoteRecord> findByTopicId(String topicId);

    List<VoteRecord> findByPropertyKeyAndPropertyValue(String key, String value);

    List<VoteRecord> findByTopicIdAndType(String topicId, String type);

    List<VoteRecord> findByTopicIdAndType(String topicId, String type, Sort sort);

    List<VoteRecord> findByType(String type);

    List<VoteRecord> findByTypeAndHeightIsGreaterThanEqual(String type, Long height);

    List<VoteRecord> findByTopicIdAndTypeAndHeightBetween(String topicId, String type, Long minHeight, Long maxHeight);

    @Query(value="SELECT MAX(height) from VoteRecord")
    Long getMaxHeight();
}
