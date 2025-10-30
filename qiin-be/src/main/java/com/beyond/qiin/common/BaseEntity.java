package com.beyond.qiin.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint")
    private Long id;

    @Comment("생성자")
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy = 0L;

    @Comment("생성 시각")
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant createdAt;

    @Comment("수정자")
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy = 0L;

    @Comment("수정 시각")
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant updatedAt;

    @Comment("삭제자")
    @Column(name = "deleted_by")
    private Long deletedBy;

    @Comment("삭제 시각")
    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP(6)")
    private Instant deletedAt;

    public void delete(final Long deleterId) {
        if (this.deletedAt != null) {
            return; // TODO: 혹은 "이미 삭제된 데이터입니다." 같은 예외처리
        }
        this.deletedAt = Instant.now();
        this.deletedBy = deleterId;
    }
}
