-- Initial data for development and testing
-- Users are now handled by DataInitializer to ensure BCrypt correctness

-- Insert sample courses
INSERT INTO courses (name, category) VALUES
  ('Spring Boot 3', 'Backend'),
  ('Vue.js 3', 'Frontend'),
  ('Docker Basics', 'DevOps');