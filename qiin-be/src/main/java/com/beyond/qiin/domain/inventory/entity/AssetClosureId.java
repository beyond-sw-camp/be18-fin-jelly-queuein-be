package com.beyond.qiin.domain.inventory.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class AssetClosureId implements Serializable {

    private Long ancestorId;

    private Long descendantId;


    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AssetClosureId that = (AssetClosureId) o;
        return Objects.equals(ancestorId, that.ancestorId) && Objects.equals(descendantId,
                                                                             that.descendantId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(ancestorId);
        result = 31 * result + Objects.hashCode(descendantId);
        return result;
    }


}
