package com.forumhub.modules.answer.service;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository repository;
    private final TopicRepository topicRepository;
    private final AnswerMapper mapper;
    private final com.forumhub.modules.audit.service.AuditLogService auditLogService;

    @Transactional
    public AnswerResponseDTO reply(@NonNull Long topicId, @NonNull AnswerRequestDTO dto) {
        // ... (mantém lógica de reply)
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        var author = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var answer = mapper.toEntity(dto);
        answer.setTopic(topic);
        answer.setAuthor(author);

        return mapper.toResponseDTO(repository.save(answer));
    }

    @Transactional
    public AnswerResponseDTO markAsSolution(@NonNull Long id) {
        var answer = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!answer.getTopic().getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the topic author can mark a solution");
        }

        answer.setSolution(!answer.getSolution());
        
        var topic = answer.getTopic();
        boolean hasSolution = repository.findAllByTopicId(topic.getId())
                .stream().anyMatch(r -> r.getSolution() && !r.getId().equals(answer.getId()));

        topic.setStatus((answer.getSolution() || hasSolution) ? TopicStatus.RESOLVED : TopicStatus.OPEN);

        return mapper.toResponseDTO(answer);
    }

        @Transactional

        public void delete(@NonNull Long id, String justification) {

            var answer = repository.findById(id)

                    .orElseThrow(() -> new EntityNotFoundException("Answer not found"));

            

            validateOwnerOrModerator(answer.getAuthor());

            

            auditLogService.logAction("DELETE", "ANSWER", answer.getId(), answer.getMessage(), justification);

            repository.delete(answer);

        }

    

        private void validateOwnerOrModerator(User author) {

            var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            boolean isModerator = currentUser.getAuthorities().stream()

                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MODERATOR"));

            

            if (!isModerator && !author.getId().equals(currentUser.getId())) {

                throw new AccessDeniedException("Permission denied");

            }

        }

    }

    