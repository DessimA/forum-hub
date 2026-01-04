package com.forumhub.modules.favorite.entity;

import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFavorite {

    @EmbeddedId
    private UserFavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("topicId")
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserFavorite(User user, Topic topic) {
        this.user = user;
        this.topic = topic;
        this.id = new UserFavoriteId(user.getId(), topic.getId());
        this.createdAt = LocalDateTime.now();
    }
}
