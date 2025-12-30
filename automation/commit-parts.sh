#!/bin/bash

# Script para realizar commits exaustivos de TODAS as alterações no FórumHub

# 1. Infraestrutura, Base de Dados e Automação
git add .gitignore \
        automation/cleanup_final.ps1 \
        backend/src/main/resources/db/migration/V1__create-initial-tables.sql \
        backend/src/main/resources/db/migration/V2__insert-admin-user.sql \
        backend/src/main/resources/db/migration/V4__add_audit_logs_and_moderation.sql \
        backend/src/test/resources/application-test.yml
git commit -m "infra: stabilize Flyway migrations and core automation

- Replace MySQL-specific UUID_TO_BIN with standard hex literals X'...' in V2
- Stabilize V1 and V4 audit_logs table definitions for UUID parity
- Enable Flyway repair-on-migrate and MySQL compatibility mode in H2
- Update gitignore and cleanup scripts"

# 2. Segurança e Módulo de Usuário (Backend Core)
git add backend/src/main/java/com/forumhub/modules/user/controller/UserController.java \
        backend/src/main/java/com/forumhub/modules/user/dto/UserResponseDTO.java \
        backend/src/main/java/com/forumhub/modules/user/model/Profile.java \
        backend/src/main/java/com/forumhub/modules/user/model/User.java \
        backend/src/main/java/com/forumhub/modules/user/repository/ProfileRepository.java \
        backend/src/main/java/com/forumhub/modules/user/service/UserService.java \
        backend/src/main/java/com/forumhub/modules/user/repository/UserRepository.java \
        backend/src/main/java/com/forumhub/security/TokenService.java \
        backend/src/main/java/com/forumhub/security/AuthService.java \
        backend/src/main/java/com/forumhub/security/SecurityFilter.java \
        backend/src/main/java/com/forumhub/security/SecurityValidationService.java \
        backend/src/main/java/com/forumhub/config/DataInitializer.java \
        backend/src/main/java/com/forumhub/exception/ResourceNotFoundException.java
git commit -m "refactor(security): stabilize User module and security filters

- Standardize findByEmail to return Optional<User> across all security layers
- Fix UUID handling in Profile and User models
- Implement robust token recovery and validation logic
- Add missing ResourceNotFoundException and fix DataInitializer"

# 3. Módulos de Curso e Tópico (Backend Business)
git add backend/src/main/java/com/forumhub/modules/course/controller/CourseController.java \
        backend/src/main/java/com/forumhub/modules/course/dto/CourseResponseDTO.java \
        backend/src/main/java/com/forumhub/modules/course/mapper/CourseMapper.java \
        backend/src/main/java/com/forumhub/modules/course/model/Course.java \
        backend/src/main/java/com/forumhub/modules/course/repository/CourseRepository.java \
        backend/src/main/java/com/forumhub/modules/topic/controller/TopicController.java \
        backend/src/main/java/com/forumhub/modules/topic/dto/TopicRequestDTO.java \
        backend/src/main/java/com/forumhub/modules/topic/dto/TopicResponseDTO.java \
        backend/src/main/java/com/forumhub/modules/topic/dto/TopicDetailResponseDTO.java \
        backend/src/main/java/com/forumhub/modules/topic/model/Topic.java \
        backend/src/main/java/com/forumhub/modules/topic/repository/TopicRepository.java \
        backend/src/main/java/com/forumhub/modules/topic/service/TopicService.java
git commit -m "feat(topic): implement mandatory pagination and UUID-ready models

- Add pagination support to Course listing
- Fix Topic record property accessors and method naming consistency
- Ensure Topic/Course entities use BINARY(16) compatible UUIDs"

# 4. Módulos de Resposta e Auditoria (Backend Support)
git add backend/src/main/java/com/forumhub/modules/answer/controller/AnswerController.java \
        backend/src/main/java/com/forumhub/modules/answer/dto/AnswerResponseDTO.java \
        backend/src/main/java/com/forumhub/modules/answer/model/Answer.java \
        backend/src/main/java/com/forumhub/modules/answer/repository/AnswerRepository.java \
        backend/src/main/java/com/forumhub/modules/answer/service/AnswerService.java \
        backend/src/main/java/com/forumhub/modules/audit/controller/AuditLogController.java \
        backend/src/main/java/com/forumhub/modules/audit/model/AuditLog.java \
        backend/src/main/java/com/forumhub/modules/audit/repository/AuditLogRepository.java \
        backend/src/main/java/com/forumhub/modules/audit/service/AuditLogService.java
git commit -m "feat(audit): stabilize answer management and data recovery

- Refine Answer model and repository for UUID parity
- Implement data restoration logic in AuditLogService using UUID strings
- Synchronize AnswerService logic with mandatory security checks"

# 5. Padronização para Inglês (Auth Standard)
git add backend/src/main/java/com/forumhub/modules/auth/controller/AuthController.java \
        backend/src/main/java/com/forumhub/modules/auth/dto/LoginRequestDTO.java \
        frontend/src/types/auth.ts \
        frontend/src/views/auth/LoginView.vue \
        frontend/src/views/auth/RegisterView.vue
git commit -m "chore: full English standardization for authentication

- Rename 'senha' to 'password' and 'nome' to 'name' in DTOs and Controllers
- Align Frontend types and views with Backend English naming conventions"

# 6. Refinamento de UI/UX do Frontend
git add frontend/src/assets/background.jpg \
        frontend/src/components/layout/TheNavbar.vue \
        frontend/src/router/index.ts \
        frontend/src/services/api.ts \
        frontend/src/stores/topic.ts \
        frontend/src/stores/course.ts \
        frontend/src/App.vue \
        frontend/src/views/topic/TopicListView.vue \
        frontend/src/views/topic/TopicDetailView.vue \
        frontend/src/views/user/ProfileView.vue \
        frontend/src/views/user/UserManagementView.vue \
        frontend/src/views/admin/CourseManagementView.vue \
        frontend/src/views/admin/AdminLogsView.vue
git commit -m "feat(frontend): Clean Dark UI refinement and background fix

- Implement sober aesthetic with reduced neon effects
- Fix background image visibility via layer transparency
- Add CourseStore and refine API service abstraction"

# 7. Testes Automatizados (Backend)
git add backend/src/test/java/com/forumhub/modules/answer/service/AnswerServiceTest.java \
        backend/src/test/java/com/forumhub/modules/audit/service/AuditLogServiceTest.java \
        backend/src/test/java/com/forumhub/modules/user/UserIntegrationTest.java \
        backend/src/test/java/com/forumhub/modules/user/controller/UserControllerTest.java \
        backend/src/test/java/com/forumhub/security/TokenServiceTest.java \
        backend/src/test/java/com/forumhub/modules/answer/service/AnswerBusinessRulesTest.java \
        backend/src/test/java/com/forumhub/modules/audit/AuditRecoveryScenarioTest.java \
        backend/src/test/java/com/forumhub/modules/auth/ \
        backend/src/test/java/com/forumhub/modules/topic/TopicResolutionFlowIntegrationTest.java \
        backend/src/test/java/com/forumhub/modules/course/service/CourseServiceTest.java \
        backend/src/test/java/com/forumhub/modules/topic/service/TopicServiceTest.java \
        backend/src/test/java/com/forumhub/modules/topic/service/TopicServiceUniquenessTest.java \
        backend/src/test/java/com/forumhub/modules/topic/controller/TopicControllerTest.java \
        backend/src/test/java/com/forumhub/modules/course/controller/CourseControllerTest.java \
        backend/src/test/java/com/forumhub/modules/audit/AuditIntegrationTest.java \
        backend/src/test/java/com/forumhub/modules/topic/TopicIntegrationTest.java \
        backend/src/test/java/com/forumhub/modules/answer/AnswerIntegrationTest.java \
        backend/src/test/java/com/forumhub/security/BlockedUserSecurityTest.java \
        backend/src/test/java/com/forumhub/security/SecurityFilterTest.java \
        backend/src/test/java/com/forumhub/security/SecurityValidationServiceTest.java \
        backend/src/test/java/com/forumhub/modules/user/service/UserServiceTest.java
git commit -m "test(backend): stabilize test suite and integration flows

- Fix NullPointerExceptions in Service tests through proper mocking
- Update tests to handle Optional<User> and UUID conversions
- Ensure database isolation and user creation in Integration tests"

# 8. Testes Automatizados (Frontend) e Scripts
git add frontend/src/components/__tests__/TheNavbar.spec.ts \
        frontend/src/views/__tests__/TopicDetailView.spec.ts \
        frontend/src/stores/__tests__/course.spec.ts \
        frontend/src/stores/__tests__/auth.spec.ts \
        frontend/src/stores/__tests__/auth_errors.spec.ts \
        automation/commit-parts.sh
git commit -m "test(frontend): sync test suite with design and naming changes

- Update auth tests to use 'password' field
- Adjust component tests for Clean Dark UI changes
- Add CourseStore unit tests"

echo "Absolutamente todas as alterações foram commitadas em detalhes!"