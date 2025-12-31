package com.forumhub.modules.topic.repository;

import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;

public class TopicSpecification {

    public static Specification<Topic> filterBy(UUID courseId, TopicStatus status, Integer year, String title) {
        return (root, query, builder) -> {
            var predicate = builder.conjunction();

            if (courseId != null) {
                predicate = builder.and(predicate, builder.equal(root.get("course").get("id"), courseId));
            }

            if (status != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), status));
            }

            if (title != null && !title.isBlank()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (year != null) {
                // Extrai o ano da data de criação
                var yearExpression = builder.function("year", Integer.class, root.get("createdAt"));
                predicate = builder.and(predicate, builder.equal(yearExpression, year));
            }

            return predicate;
        };
    }
}
