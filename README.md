# 18-Final-Jelly-QueueIn-BE

<br>

## 📑 목차
1. [팀원](#team)
2. [프로젝트 기획서](#plan)
3. [요구사항 정의서](#requirements)
4. [시스템 아키텍처](#architecture)
5. [WBS](#wbs)
6. [ERD](#erd)
7. [화면 설계서](#uiux)
8. [백엔드 단위 테스트](#backend-test)
9. [UI/UX 단위 테스트](#uiux-test)
10. [API 명세서](#api)
11. [통합테스트 결과서](#integration-test)
12. [CI/CD 계획서](#cicd)
---

<a id="team"></a>
## ✨ 팀원

<table style="width:100%;">
  <thead>
    <tr align="center">
      <th>최정필</th>
      <th>윤동기</th>
      <th>김민준</th>
      <th>박채연</th>
     </tr>
  </thead>
   <tbody>
    <tr align="center">
      <td>
       <img src="https://github.com/user-attachments/assets/efe5a9fd-d9d4-4cbc-8947-110c6c3ee5c4" height = "150px" width="100" alt="최정필 프로필" />
      </td>
      <td>
       <img src="https://github.com/user-attachments/assets/a4fffe12-4935-45ea-9c4f-a9de1cad7dd8" height = "150px" width="100" alt="윤동기 프로필" />
      </td>
      <td>
       <img src="https://github.com/user-attachments/assets/c6dadd80-18e8-4472-b995-2aef49bc34a7" height = "150px" width="100" alt="김민준 프로필"/>
      </td>
      <td>
       <img src="https://pbs.twimg.com/media/Dl7_PlnU4AEh-WC.jpg" height = "150px" width="100" alt="박채연 프로필" />
      </td>
      </tr>
    <tr align="center">
      <td>
        <a href="https://github.com/wjdvlf5456" target="_blank">
        <img src="https://img.shields.io/badge/GitHub-Link-black?logo=github" alt="GitHub Link"/></a>
      </td>
      <td>
        <a href="https://github.com/ydg010" target="_blank">
        <img src="https://img.shields.io/badge/GitHub-Link-black?logo=github" alt="GitHub Link"/></a>
      </td>
      <td>
        <a href="https://github.com/promandu" target="_blank">
        <img src="https://img.shields.io/badge/GitHub-Link-black?logo=github" alt="GitHub Link"/></a>
      </td>
      <td>
        <a href="https://github.com/pcochoco" target="_blank">
        <img src="https://img.shields.io/badge/GitHub-Link-black?logo=github" alt="GitHub Link"/></a> 
      </td>
    </tr>
    </tbody>
  </table>
  <br>

---
  
## <a id="plan"></a>1. 프로젝트 기획서
  
  <details>
    <summary>세부사항</summary>
    <div markdown="1">
      <a href="https://www.notion.so/playdatacademy/28fd943bcac28025b2dccb788a358e2d">프로젝트 기획서</a>
    </div>
  </details>
  <br>
  
## <a id="requirements"></a>2. 요구사항 정의서
  
  <details>
    <summary>세부사항</summary>
    <div markdown="1">
     <a href="https://docs.google.com/spreadsheets/d/1EP02kiox0N3FR3h3L8dLPgaPCmodeS3FejvRLaDl5R4/edit?gid=1474691015#gid=1474691015">요구사항 정의서</a>
    </div>
  </details>
  <br>
  
## <a id="requirements"></a>3. 시스템 아키텍처
  
  <details>
    <summary>세부사항</summary>
    <div markdown="1">
     <a href="https://www.figma.com/design/xXNsXQ9pTSdpUywONBOBTr/QueueIn?node-id=28-1861&p=f&t=ikXIls49kJ8eajIo-0">시스템 아키텍처</a>
    </div>
    <div markdown="1">
      <img width="1581" height="795" alt="Frame 12" src="https://github.com/user-attachments/assets/66b17403-a2c3-43b7-b664-a2a26487ae2a" />
    </div>
  </details>
  <br>
  
## <a id="requirements"></a>4. WBS
  
  <details>
     <summary>세부사항</summary>
    <div markdown="1">
      <a href="https://docs.google.com/spreadsheets/d/1EP02kiox0N3FR3h3L8dLPgaPCmodeS3FejvRLaDl5R4/edit?gid=448418800#gid=448418800">WBS</a>
    </div>
    <div markdown="1">
      <img width="1842" height="700" alt="image" src="https://github.com/user-attachments/assets/eb4b0faa-421a-454c-8182-d07661258239" />
    </div>
  </details>
  <br>
  
## <a id="requirements"></a>5. ERD
  
  <details>
    <summary>세부사항</summary>
    <div markdown="1">
      <a href="https://www.erdcloud.com/d/fFbaSvk2sHJQgE6RS">ERD Cloud</a>
    </div>
    <div markdown="1">
        <img width="1077" height="817" alt="KakaoTalk_20251114_170311228" src="https://github.com/user-attachments/assets/4b6862ac-1109-47e6-b0e4-ebf882e34457" />
    </div>
  </details>
  <br>


## <a id="requirements"></a>6. 화면 설계서
  
  <details>
    <summary>세부사항</summary>
    <div markdown="1">
     <a href="https://www.figma.com/design/xXNsXQ9pTSdpUywONBOBTr/QueueIn?node-id=0-1&p=f&t=ikXIls49kJ8eajIo-0">화면 설계서</a>
    </div>
  </details>
  <br>

## <a id="requirements"></a>7. 백엔드 단위 테스트
<details> <summary><strong>IAM - Auth</strong></summary>

JwtTokenProviderTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/auth/JwtTokenProviderTest.png" />

<br> </details>
<details> <summary><strong>IAM - User</strong></summary>

UserController 단위 테스트
<img width="1475" src="docs/backend_images/iam/controller/UserControllerTest.png" />

<br>

UserCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/command/UserServiceServiceImplTest.png" />

<br>

UserQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/query/UserQueryServiceImplTest.png" />

<br> </details>
<details> <summary><strong>IAM - Role</strong></summary>

RoleController 단위 테스트
<img width="1475" src="docs/backend_images/iam/controller/RoleControllerTest.png" />

<br>

RoleCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/command/RoleCommandServiceImplTest.png" />

<br>

RolePermissionCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/command/RolePermissionCommandServiceImplTest.png" />

<br>

RoleQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/query/RoleQueryServiceImplTest.png" />

<br> </details>
<details> <summary><strong>IAM - Permission</strong></summary>

PermissionController 단위 테스트
<img width="1475" src="docs/backend_images/iam/controller/PermissionControllerTest.png" />

<br>

PermissionCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/command/PermissionCommandServiceImplTest.png" />

<br>

PermissionQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/iam/service/query/PermissionQueryServiceImplTest.png" />

<br> </details>
<details> <summary><strong>Booking - Reservation</strong></summary>
Query

IsReservableForDayTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/query/IsReservableForDayTest.png" />

<br>
Command

UpdateReservationForAssetTestTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/command/UpdateReservationForAssetTestTest.png" />

<br>

StartReservationTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/command/StartReservationTest.png" />

<br>

ReservationTimeValidationTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/command/ReservationTimeValidationTest.png" />

<br>

InstantConfirmReservationTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/command/InstantConfirmReservationTest.png" />

<br>

EndReservationTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/command/EndReservationTest.png" />

<br>

ApproveReservationTest 단위 테스트
<img width="1475" src="docs/backend_images/booking/service/command/ApproveReservationTest.png" />

<br>
</details>
<details> <summary><strong>Inventory - Asset</strong></summary>
Command

AssetCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/inventory/service/command/AssetCommandServiceImplTest.png" />

<br>
Query

AssetQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/inventory/service/query/AssetQueryServiceImplTest.png" />

<br>
</details>
<details> <summary><strong>Inventory - Category</strong></summary>
Command

CategoryCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/inventory/service/command/CategoryCommandServiceImplTest.png" />

<br>
Query

CategoryQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/inventory/service/query/CategoryQueryServiceImplTest.png" />

<br>
</details>
<details> <summary><strong>Accounting</strong></summary>
Query

SettlementPerformanceQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/query/SettlementPerformanceQueryServiceImplTest.png" />

<br>

SettlementQuarterQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/query/SettlementQuarterQueryServiceImplTest.png" />

<br>

UsageHistoryQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/query/UsageHistoryQueryServiceImplTest.png" />

<br>

UsageHistoryTrendQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/query/UsageHistoryTrendQueryServiceImplTest.png" />

<br>

UsageTargetQueryServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/query/UsageTargetQueryServiceImplTest.png" />

<br>
Command

UserHistoryCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/command/UserHistoryCommandServiceImplTest.png" />

<br>

UsageTargetCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/command/UsageTargetCommandServiceImplTest.png" />

<br>

UsageHistoryCommandServiceImpl 테스트
<img width="1475" src="docs/backend_images/accounting/service/command/UsageHistoryCommandServiceImpl.png" />

<br>

SettlementCommandServiceImplTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/service/command/SettlementCommandServiceImplTest.png" />

<br>
Controller

UsageTargetController 단위 테스트
<img width="1475" src="docs/backend_images/accounting/controller/UsageTargetController.png" />

<br>

UsageHistoryControllerTest 단위 테스트
<img width="1475" src="docs/backend_images/accounting/controller/UsageHistoryControllerTest.png" />

<br>

SettlementController 단위 테스트
<img width="1475" src="docs/backend_images/accounting/controller/SettlementController.png" />

<br>
</details>

## <a id="requirements"></a>8. UI/UX 단위 테스트

<details>
  <summary><strong>IAM</strong></summary>

<details>
<summary><strong>auth</strong></summary>

로그인 화면
<img width="1475" src="docs/iam/auth/로그인화면.png" />

<br>

비밀번호 변경 후 재로그인 화면
<img width="1475" src="docs/iam/auth/비밀번호변경후재로그인.png" />

<br>

임시 비밀번호 발급 화면
<img width="1475" src="docs/iam/auth/임시비밀번호발급.png" />

<br>

임시 비밀번호 변경 화면
<img width="1475" src="docs/iam/auth/임시비밀번호변경.png" />

<br> </details>
<br>
<details>
  <summary><strong>user</strong></summary>

- 사용자 목록 조회  
  <img width="1475" height="868" alt="image" src="docs/iam/user/사용자목록조회.png" />

<br>

- 사용자 신규 등록  
  <img width="1475" src="./docs/iam/user/사용자등록.png" />

<br>

- 사용자 수정 (관리자용)  
  <img width="1475" src="docs/iam/user/사용자수정_관리자용.png" />

<br>

</details>

<br>

<details>
  <summary><strong>role</strong></summary>

- 역할 목록 조회  
  <img width="1475" src="docs/iam/role/역할목록조회.png" />

<br>

> 역할 목록 조회 상세(토글)  
<img width="1475" src="docs/iam/role/역할목록조회_토글.png" />

<br>

- 역할 추가  
  <img width="1475" src="docs/iam/role/역할등록.png" />

<br>

</details>

<br>

<details>
  <summary><strong>permission</strong></summary>

- 권한 목록 조회  
  <img width="1475" src="docs/iam/permission/권한목록조회.png" />

<br>

</details>

</details>

<details>
  <summary><strong>inventory</strong></summary>

<br>

<details>
  <summary><strong>카테고리 드롭다운</strong></summary>

- 0계층 드롭다운  
  <img width="1475" src="docs/inventory/0계층 드롭다운.png" />
  <br>

- 1계층 드롭다운  
  <img width="1475" src="docs/inventory/1계층 드롭다운.png" />
  <br>

- 카테고리 드롭다운  
  <img width="1475" src="docs/inventory/카테고리 드롭다운.png" />
  <br>

</details>

<br>

<details>
  <summary><strong>카테고리 관리</strong></summary>

- 카테고리 목록 조회  
  <img width="1475" src="docs/inventory/카테고리 목록 조회.png" />
  <br>

- 카테고리 생성  
  <img width="1475" src="docs/inventory/카테고리 생성.png" />
  <br>

- 카테고리 수정  
  <img width="1475" src="docs/inventory/카테고리 수정.png" />
  <br>

- 카테고리 삭제  
  <img width="1475" src="docs/inventory/카테고리 삭제.png" />
  <br>

</details>

<br>

<details>
  <summary><strong>자원 관리</strong></summary>

- 자원 목록 조회  
  <img width="1475" src="docs/inventory/자원 목록 조회.png" />
  <br>

- 자원 상세 정보  
  <img width="1475" src="docs/inventory/자원 상세 정보.png" />
  <br>

- 자원 생성  
  <img width="1475" src="docs/inventory/자원 생성.png" />
  <br>

- 자원 수정  
  <img width="1475" src="docs/inventory/자원 수정.png" />
  <br>

- 자원 이동  
  <img width="1475" src="docs/inventory/자원 이동.png" />
  <br>

- 자원 삭제  
  <img width="1475" src="docs/inventory/자원 삭제.png" />
  <br>

</details>

</details>

<details>
  <summary><strong>booking</strong></summary>

<br>

<details>
  <summary><strong>예약 화면</strong></summary>

- 예약 화면  
  <img width="1475" src="docs/reservation/예약.png" />
  <br>

- 자원별 예약 화면  
  <img width="1475" src="docs/reservation/자원.png" />
  <br>

- 사용자별 예약 화면  
  <img width="1475" src="docs/reservation/사용자.png" />
  <br>

- 월별 예약 화면  
  <img width="1475" src="docs/reservation/월별.png" />
  <br>

- 주별 예약 화면  
  <img width="1475" src="docs/reservation/주별.png" />
  <br>

</details>

<br>

<details>
  <summary><strong>예약 상세</strong></summary>

- 예약 상세 화면  
  <img width="1475" src="docs/reservation/예약상세.png" />
  <br>

- 예약 상세 모달/세부 정보  
  <img width="1475" src="docs/reservation/상세.png" />
  <br>

</details>

<br>

<details>
  <summary><strong>예약 생성 및 신청 관리</strong></summary>

- 참여자 추가  
  <img width="1475" src="docs/reservation/참여자추가.png" />
  <br>

- 신청 예약 관리  
  <img width="1475" src="docs/reservation/신청예약관리.png" />
  <br>

</details>

</details>




<details> <summary><strong>accounting</strong></summary> <br> <details> <summary><strong>목표 사용률</strong></summary>

목표 사용률 등록
<img width="1475" src="docs/accounting/목표 사용률 등록.png" />

<br>

목표 사용률 조회
<img width="1475" src="docs/accounting/목표 사용률 조회.png" />

<br>
</details> <br> <details> <summary><strong>분기 정산</strong></summary>

분기 정산
<img width="1475" src="docs/accounting/분기 정산.png" />

<br>
</details> <br> <details> <summary><strong>사용 추이</strong></summary>

사용 추이
<img width="1475" src="docs/accounting/사용 추이.png" />

<br>
</details> <br> <details> <summary><strong>운영 성과 분석</strong></summary>

운영 성과 분석
<img width="1475" src="docs/accounting/운영 성과 분석.png" />

<br>
</details> <br> <details> <summary><strong>자원 사용 기록</strong></summary>

자원 사용 기록
<img width="1475" src="docs/accounting/자원 사용 기록.png" />

<br>

자원 사용 기록 상세 모달
<img width="1475" src="docs/accounting/자원 사용 기록 상세 모달.png" />

<br>
</details> </details>


## <a id="requirements"></a>9. API 명세서
[API 명세서](https://ijn7tz4u5x.apidog.io/)

## <a id="requirements"></a>10. 통합테스트 결과서


## <a id="requirements"></a>11. CICD 계획서
[CICD 계획서](https://nice-surprise-7e8.notion.site/CI-CD-2c50b5bf3ee080b082ccfbc4bbf10f36)