package com.beyond.qiin.domain.inventory.entity;

import com.beyond.qiin.common.BaseEntity;
import com.beyond.qiin.domain.inventory.dto.category.request.UpdateCategoryRequestDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "category")
@AttributeOverride(name = "id", column = @Column(name = "category_id"))
@SQLRestriction("deleted_at IS NULL")
public class Category extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    public void apply(UpdateCategoryRequestDto requestDto) {
        if(requestDto.getName() != null) this.name = requestDto.getName();
        if(requestDto.getDescription() != null) this.description = requestDto.getDescription();
    }
}
