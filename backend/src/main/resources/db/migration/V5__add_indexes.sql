-- Create indexes for frequently queried columns
CREATE INDEX idx_topics_author ON topics(author_id);
CREATE INDEX idx_topics_course ON topics(course_id);
CREATE INDEX idx_topics_status ON topics(status);
CREATE INDEX idx_topics_created ON topics(created_at);

CREATE INDEX idx_answers_topic ON answers(topic_id);
CREATE INDEX idx_answers_author ON answers(author_id);
CREATE INDEX idx_answers_solution ON answers(solution);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_performer ON audit_logs(performed_by);
