package com.beyond.qiin.domain.iam.controller;

import com.beyond.qiin.domain.iam.dto.department.request.CreateDepartmentRequestDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentListResponseDto;
import com.beyond.qiin.domain.iam.dto.department.response.DepartmentResponseDto;
import com.beyond.qiin.domain.iam.service.command.DepartmentCommandService;
import com.beyond.qiin.domain.iam.service.query.DepartmentQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentCommandService departmentCommandService;
    private final DepartmentQueryService departmentQueryService;

    // 부서 생성
    @PostMapping
    public ResponseEntity<DepartmentResponseDto> createDepartment(
            final @Valid @RequestBody CreateDepartmentRequestDto request) {
        return ResponseEntity.ok(departmentCommandService.createDepartment(request));
    }
    // 부서명 수정
    // @PatchMapping

    // 부서 관계 수정
    // @PatchMapping

    // 부서 삭제 (하위 부서 있을 시 삭제 불가 예외 처리)
    // @DeleteMapping

    @GetMapping("/departments")
    public ResponseEntity<DepartmentListResponseDto> getDepartments() {
        return ResponseEntity.ok(departmentQueryService.getDepartments());
    }
}
