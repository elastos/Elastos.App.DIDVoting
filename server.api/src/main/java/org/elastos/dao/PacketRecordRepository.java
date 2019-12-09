package org.elastos.dao;

import org.elastos.dto.PacketRecord;
import org.elastos.dto.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PacketRecordRepository extends JpaRepository<PacketRecord, Long> {
    Optional<PacketRecord> findByTopicId(String topicId);
}
