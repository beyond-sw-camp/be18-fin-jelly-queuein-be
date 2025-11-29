package com.beyond.qiin.domain.iam.dto.user.request.search_condition;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUsersSearchCondition {

    private String userName;
    private String email;
    private Long dptId;
    private String roleName;

    private LocalDate hireDateStart;
    private LocalDate hireDateEnd;
}
