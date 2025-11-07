package com.beyond.qiin.domain.iam.controller.command;

import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentDetailResponseDto;
import com.beyond.qiin.domain.iam.service.command.DepartmentCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentCommandController {

    private final DepartmentCommandService departmentCommandService;

    // 부서 생성
    @PostMapping
    public ResponseEntity<DepartmentDetailResponseDto> createDepartment(
            final @Valid @RequestBody CreateDepartmentRequestDto request) {
        return ResponseEntity.ok(departmentCommandService.createDepartment(request));
    }
    // 부서명 수정
    // @PatchMapping

    // 부서 관계 수정
    // @PatchMapping

    // 부서 삭제 (하위 부서 있을 시 삭제 불가 예외 처리)
    // @DeleteMapping

}
