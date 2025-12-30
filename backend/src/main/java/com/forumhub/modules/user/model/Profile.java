package com.forumhub.modules.user.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    public Profile() {}

    public Profile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
