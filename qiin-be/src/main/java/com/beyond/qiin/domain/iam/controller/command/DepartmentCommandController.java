package com.beyond.qiin.domain.iam.controller.command;

import com.beyond.qiin.domain.iam.service.command.DepartmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentCommandController {

    private final DepartmentCommandService departmentCommandService;

    //  // 부서 생성
    //  @PostMapping
    //  public ResponseEntity<BomDetailResponse> createBom(
    //      final @Valid @RequestBody CreateBomRequest request) {
    //    return ResponseEntity.ok(departmentCommandService.createBom(request));
    //  }

}
