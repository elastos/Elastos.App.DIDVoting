package org.elastos.dto;

import javax.persistence.*;

@Entity
@Table(name="vote_record",indexes = {@Index(name = "vote_record_topic_id_index", columnList = "topic_id"),
        @Index(name = "vote_record_height_index", columnList = "height"),
        @Index(name = "vote_record_type_index", columnList = "type")})
public class VoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="service_did", nullable = false, length = 100)
    private String serviceDid;
    @Column(name="topic_id", nullable = false, length = 100)
    private String topicId;
    @Column(name="type", nullable = false, length = 50)
    private String type;//VoteTopicType
    @Column(name="property_key", nullable = false, columnDefinition = "Text")
    private String propertyKey;
    @Column(name="property_value", nullable = false, columnDefinition = "Text")
    private String propertyValue;
    @Column(name="content", nullable = false, columnDefinition = "Text")
    private String content;
    @Column(name="height", nullable = false)
    private Long height;
    @Column(name="public_key", nullable = false, length = 100)
    private String publicKey;
    @Column(name="did", nullable = false, length = 100)
    private String did;
    @Column(name="did_height", nullable = false)
    private Long didHeight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceDid() {
        return serviceDid;
    }

    public void setServiceDid(String serviceDid) {
        this.serviceDid = serviceDid;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public Long getDidHeight() {
        return didHeight;
    }

    public void setDidHeight(Long didHeight) {
        this.didHeight = didHeight;
    }
}
