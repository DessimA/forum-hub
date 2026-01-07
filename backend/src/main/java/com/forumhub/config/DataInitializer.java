package com.forumhub.config;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@org.springframework.context.annotation.Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting Data Initialization...");

        // 1. Ensure Profiles
        Profile adminProfile = ensureProfile("ROLE_ADMIN", "550e8400-e29b-41d4-a716-446655440000");
        Profile moderatorProfile = ensureProfile("ROLE_MODERATOR", "550e8400-e29b-41d4-a716-446655440001");
        Profile userProfile = ensureProfile("ROLE_USER", "550e8400-e29b-41d4-a716-446655440002");

        // 2. Ensure Admin
        User admin = userRepository.findByEmail("admin@forumhub.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("José Anderson (Admin)")
                        .email("admin@forumhub.com")
                        .password(passwordEncoder.encode("admin123"))
                        .profile(adminProfile)
                        .isPermanentBlocked(false)
                        .build()));

        // 3. Ensure Other Users
        if (userRepository.findByEmail("moderador@forumhub.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Alice Moderadora")
                    .email("moderador@forumhub.com")
                    .password(passwordEncoder.encode("admin123"))
                    .profile(moderatorProfile)
                    .build());
        }

        String[] names = {"Lucas Silva", "Maria Oliveira", "Carlos Souza", "Ana Costa", "Roberto Mendes", "Julia Rocha", "Fernando Dias", "Sofia Lima"};
        for (int i = 0; i < names.length; i++) {
            String email = "user" + (i + 1) + "@example.com";
            if (userRepository.findByEmail(email).isEmpty()) {
                userRepository.save(User.builder()
                        .name(names[i])
                        .email(email)
                        .password(passwordEncoder.encode("admin123"))
                        .profile(userProfile)
                        .build());
            }
        }

        // 4. Ensure Courses
        List<Course> coursesToSeed = List.of(
            new Course(UUID.randomUUID(), "Comunidade e Suporte", "Geral", null),
            new Course(UUID.randomUUID(), "Spring Boot 3", "Backend", null),
            new Course(UUID.randomUUID(), "Vue.js 3", "Frontend", null),
            new Course(UUID.randomUUID(), "React & Next.js", "Frontend", null),
            new Course(UUID.randomUUID(), "Java Advanced", "Backend", null),
            new Course(UUID.randomUUID(), "JavaScript Moderno", "Frontend", null),
            new Course(UUID.randomUUID(), "Flutter Avançado", "Mobile", null),
            new Course(UUID.randomUUID(), "NestJS Enterprise", "Backend", null),
            new Course(UUID.randomUUID(), "Docker & Kubernetes", "Infraestrutura", null),
            new Course(UUID.randomUUID(), "AWS Cloud Foundation", "Cloud", null),
            new Course(UUID.randomUUID(), "Python para Dados", "Data Science", null),
            new Course(UUID.randomUUID(), "Machine Learning 101", "Inteligência Artificial", null),
            new Course(UUID.randomUUID(), "SQL Avançado e Tuning", "Banco de Dados", null),
            new Course(UUID.randomUUID(), "Cibersegurança Prática", "Segurança", null),
            new Course(UUID.randomUUID(), "Clean Architecture", "Arquitetura", null),
            new Course(UUID.randomUUID(), "Microservices Patterns", "Arquitetura", null),
            new Course(UUID.randomUUID(), "Gestão de Carreira", "Soft Skills", null),
            new Course(UUID.randomUUID(), "Liderança Técnica", "Soft Skills", null),
            new Course(UUID.randomUUID(), "Outros Assuntos", "Outros", null)
        );

        for (Course c : coursesToSeed) {
            if (!courseRepository.existsByName(c.getName())) {
                courseRepository.save(c);
                log.info("Seeded course: {}", c.getName());
            }
        }

        // 5. Ensure Topics (Check existence for EACH topic)
        List<User> allUsers = userRepository.findAll();
        Course geral = courseRepository.findByName("Comunidade e Suporte");
        Course others = courseRepository.findByName("Outros Assuntos");

        if (geral == null || others == null) {
            log.error("Critical courses missing. Seeding aborted.");
            throw new RuntimeException("Critical courses 'Comunidade e Suporte' or 'Outros Assuntos' not found!");
        }

        // Rules Topic
        String rulesTitle = "📢 Regras da Comunidade FórumHub";
        if (topicRepository.findByTitle(rulesTitle).isEmpty()) {
            topicRepository.save(Topic.builder()
                    .title(rulesTitle)
                    .message("Bem-vindos! Para garantir uma convivência saudável: 1. Respeite todos. 2. Evite duplicidade. 3. Use tags corretas. 4. Poste código formatado.")
                    .author(admin)
                    .course(geral)
                    .status(TopicStatus.OPEN)
                    .build());
            log.info("Seeded Rules topic");
        }

        // 25 Varied Topics
        String[][] topicData = {
            {"Dúvida sobre Spring Security", "Como configuro múltiplos filtros de autenticação no Spring 3.4?", "user1@example.com", "Spring Boot 3"},
            {"Vue 3 vs React em 2026", "Qual framework vocês acham que está mais maduro para aplicações enterprise hoje?", "user2@example.com", "Vue.js 3"},
            {"Erro ao subir container Docker", "O MySQL não consegue criar o volume persistente no Windows.", "user3@example.com", "Docker & Kubernetes"},
            {"Dicas para o primeiro emprego Tech", "Compartilhem suas experiências de como conseguiram a primeira vaga como Dev.", "moderador@forumhub.com", "Gestão de Carreira"},
            {"Performance em Java Records", "Existe alguma perda de performance ao usar Records em vez de Classes tradicionais?", "user4@example.com", "Java Advanced"},
            {"Novidades do JavaScript ES2025", "O que vocês estão achando das novas propostas de sintaxe para decoradores?", "user5@example.com", "JavaScript Moderno"},
            {"State Management no Flutter", "BLoc ou Riverpod? Qual a melhor escolha para projetos escaláveis?", "user6@example.com", "Flutter Avançado"},
            {"Arquitetura NestJS", "Como aplicar Clean Architecture de forma eficiente dentro do ecossistema Nest?", "user7@example.com", "NestJS Enterprise"},
            {"Python no Backend?", "Vale a pena usar FastAPI em vez de Node.js para APIs de alta performance?", "user8@example.com", "Python para Dados"},
            {"AWS Lambda vs Kubernetes", "Para uma API pequena, qual o custo-benefício de Serverless?", "user1@example.com", "AWS Cloud Foundation"},
            {"O que é RAG em AI?", "Como implementar Retrieval-Augmented Generation usando Python?", "user2@example.com", "Machine Learning 101"},
            {"Indexação em MySQL", "Como identificar quais colunas precisam de índice em uma query complexa?", "user3@example.com", "SQL Avançado e Tuning"},
            {"Dúvida sobre CSS Transitions", "Minhas animações no Vuetify estão travando em dispositivos mobile.", "user4@example.com", "Vue.js 3"},
            {"Configuração de CI/CD", "Como configurar o GitHub Actions para fazer deploy automático no Kubernetes?", "user5@example.com", "Docker & Kubernetes"},
            {"Criptografia de Senhas", "Qual the best salt size para usar com BCrypt no Java?", "user6@example.com", "Cibersegurança Prática"},
            {"Microservices vs Monólito Modular", "Em que ponto a complexidade compensa a mudança?", "user7@example.com", "Microservices Patterns"},
            {"Transição para Tech Lead", "Quais os principais desafios de parar de codar tanto e focar em pessoas?", "user8@example.com", "Liderança Técnica"},
            {"Assunto Aleatório", "Alguém aí gosta de café especial? Qual o melhor grão para o dia a dia?", "user1@example.com", "Outros Assuntos"},
            {"Testes Unitários com Vitest", "Melhores práticas para mockar stores do Pinia em testes de componentes.", "user2@example.com", "Vue.js 3"},
            {"Carreira: Especialista ou Generalista?", "Devo focar apenas em uma stack ou conhecer um pouco de tudo?", "user3@example.com", "Gestão de Carreira"},
            {"Problemas com JPA ManyToMany", "Como evitar o erro de recursão infinita na serialização JSON?", "user4@example.com", "Spring Boot 3"},
            {"React Next.js 15", "O que acharam das mudanças nos Server Components?", "user5@example.com", "React & Next.js"},
            {"Clean Code no dia a dia", "Vocês acham que os comentários são sempre um sinal de falha no código?", "moderador@forumhub.com", "Clean Architecture"},
            {"Otimização de Queries SQL", "Dicas para melhorar o tempo de resposta em tabelas com milhões de registros.", "user6@example.com", "SQL Avançado e Tuning"},
            {"Segurança em APIs REST", "Como implementar adequadamente o fluxo de Refresh Token com JWT?", "admin@forumhub.com", "Spring Boot 3"}
        };

        for (String[] data : topicData) {
            String title = data[0];
            if (topicRepository.findByTitle(title).isEmpty()) {
                String email = data[2];
                String courseName = data[3];
                
                User author = allUsers.stream()
                        .filter(u -> u.getEmail().equals(email))
                        .findFirst()
                        .orElse(admin);
                
                Course course = courseRepository.findByName(courseName);
                if (course == null) course = others;

                topicRepository.save(Topic.builder()
                        .title(title)
                        .message(data[1])
                        .author(author)
                        .course(course)
                        .status(TopicStatus.OPEN)
                        .build());
                log.info("Seeded topic: {}", title);
            }
        }
        log.info("Data Initialization Completed Successfully.");
    }

    private Profile ensureProfile(String name, String id) {
        return profileRepository.findByName(name)
                .orElseGet(() -> profileRepository.save(new Profile(UUID.fromString(id), name)));
    }
}