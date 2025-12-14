package com.beyond.qiin.domain.iam.entity;

/**
 * 개인정보이니 하드딜리트 되어야 함
 */
import com.beyond.qiin.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_profile")
@SQLRestriction("deleted_at IS NULL")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class UserProfile extends BaseEntity {

    /**
     * PK = user_profile.user_id = user.user_id
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "image_key", length = 255, nullable = false)
    private String imageKey;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    public static UserProfile create(final User user, final String imageKey, final String imageUrl) {
        return UserProfile.builder()
                .user(user)
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .build();
    }

    public void updateImage(final String imageKey, final String imageUrl) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
    }
}
