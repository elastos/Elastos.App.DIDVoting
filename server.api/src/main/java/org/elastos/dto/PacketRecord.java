package org.elastos.dto;

import javax.persistence.*;

@Entity
@Table(name="packet_record",indexes = {@Index(name = "packet_record_topic_id_index", columnList = "topic_id")})
public class PacketRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="topic_id", unique = true, nullable = false, length = 100)
    private String topicId;
    @Column(name="packet_hash", nullable = false, length = 50)
    private String packetHash;
    @Column(name="is_del", nullable = false)
    private Boolean isDel = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getPacketHash() {
        return packetHash;
    }

    public void setPacketHash(String packetHash) {
        this.packetHash = packetHash;
    }

    public Boolean getDel() {
        return isDel;
    }

    public void setDel(Boolean del) {
        isDel = del;
    }
}
