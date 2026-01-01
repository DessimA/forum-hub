package com.forumhub.modules.course;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CacheIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RedisCacheManager cacheManager;

    @Test
    void shouldCacheCourseList() {
        var pageable = PageRequest.of(0, 10);
        courseService.listAll(pageable);
        
        var cache = cacheManager.getCache("courses");
        assertThat(cache).isNotNull();
        // O cache deve conter a chave 0-10. Usamos .get(key) que retorna um ValueWrapper
        assertThat(cache.get("0-10")).isNotNull();
    }

    @Test
    void shouldEvictCacheWhenCourseCreated() {
        var pageable = PageRequest.of(0, 10);
        courseService.listAll(pageable);
        
        assertThat(Objects.requireNonNull(cacheManager.getCache("courses")).get("0-10")).isNotNull();

        // Cria novo curso - deve limpar o cache (evict)
        courseService.create(new CourseRequestDTO("Redis Test Course", "Infrastructure"));

        // O cache deve estar vazio após a criação
        assertThat(Objects.requireNonNull(cacheManager.getCache("courses")).get("0-10")).isNull();
    }
}