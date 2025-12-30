-- Insert fixed UUIDs for basic profiles
INSERT INTO profiles (id, name) VALUES (X'550e8400e29b41d4a716446655440000', 'ROLE_ADMIN');
INSERT INTO profiles (id, name) VALUES (X'550e8400e29b41d4a716446655440001', 'ROLE_MODERATOR');
INSERT INTO profiles (id, name) VALUES (X'550e8400e29b41d4a716446655440002', 'ROLE_USER');

-- Insert initial courses with fixed UUIDs for compatibility and determinism
INSERT INTO courses (id, name, category) VALUES 
  (X'a1b2c3d4e5f647a8b9c0d1e2f3a4b5c6', 'Spring Boot 3', 'Backend'),
  (X'b2c3d4e5f6a748b9c0d1e2f3a4b5c6d7', 'Vue.js 3', 'Frontend'),
  (X'c3d4e5f6a7b849c0d1e2f3a4b5c6d7e8', 'Docker Basics', 'DevOps');
