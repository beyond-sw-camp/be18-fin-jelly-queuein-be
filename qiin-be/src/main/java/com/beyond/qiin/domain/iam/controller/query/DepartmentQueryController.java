package com.beyond.qiin.domain.iam.controller.query;

import com.beyond.qiin.domain.iam.service.query.DepartmentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentQueryController {

  private final DepartmentQueryService departmentQueryService;

//  @GetMapping
//  public ResponseEntity


}
