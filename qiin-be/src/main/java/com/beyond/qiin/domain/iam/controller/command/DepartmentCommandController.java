package com.beyond.qiin.domain.iam.controller.command;

import com.beyond.qiin.domain.iam.service.command.DepartmentCommandService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
