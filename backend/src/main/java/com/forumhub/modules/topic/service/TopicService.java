package com.forumhub.modules.topic.service;

import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.*;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository repository;
    private final CourseRepository courseRepository;
    private final AnswerRepository answerRepository;
    private final com.forumhub.modules.user.repository.UserRepository userRepository;
    private final TopicMapper mapper;
    private final AnswerMapper answerMapper;
    private final com.forumhub.modules.audit.service.AuditLogService auditLogService;

    @Transactional
    public TopicResponseDTO create(@NonNull TopicRequestDTO dto) {
        var course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof User user) {
            email = user.getEmail();
        } else {
            email = principal.toString();
        }

        var author = userRepository.findByEmail(email);
        if (author == null) {
            throw new EntityNotFoundException("Author not found: " + email);
        }

        var topic = mapper.toEntity(dto);
        topic.setAuthor(author);
        topic.setCourse(course);
        topic.setStatus(TopicStatus.OPEN);

        return mapper.toResponseDTO(repository.save(topic));
    }

    public Page<TopicResponseDTO> list(@NonNull Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    public TopicDetailResponseDTO getDetail(@NonNull Long id) {
        var topic = repository.findById(id).orElseThrow();
        var answers = answerRepository.findAllByTopicId(id).stream()
                .map(answerMapper::toResponseDTO).toList();
        
        var dto = mapper.toDetailDTO(topic);
        return new TopicDetailResponseDTO(
            dto.id(), dto.title(), dto.message(), dto.createdAt(), 
            dto.status(), dto.author(), topic.getAuthor().getId(), dto.authorEmail(), dto.course(), answers
        );
    }

    @Transactional
    public TopicResponseDTO update(@NonNull Long id, @NonNull TopicUpdateDTO dto) {
        var topic = repository.findById(id).orElseThrow();
        validateOwnerOrModerator(topic.getAuthor());
        
        if (dto.title() != null) topic.setTitle(dto.title());
        if (dto.message() != null) topic.setMessage(dto.message());
        
        return mapper.toResponseDTO(topic);
    }

    @Transactional
    public void delete(@NonNull Long id, String justification) {
        var topic = repository.findById(id).orElseThrow();
        validateOwnerOrModerator(topic.getAuthor());
        
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), topic.getTitle() + ": " + topic.getMessage(), justification);
        repository.delete(topic);
    }

    private void validateOwnerOrModerator(User author) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();
        
        String email;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof User user) {
            email = user.getEmail();
        } else {
            email = principal.toString();
        }

        var currentUser = userRepository.findByEmail(email);
        if (currentUser == null) {
            throw new AccessDeniedException("User not found: " + email);
        }

        boolean isModerator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MODERATOR"));
        
        if (!isModerator && !author.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }
    }
}
