CREATE TABLE waiting_queue (
                               waiting_queue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               token VARCHAR(255) NOT NULL,
                               status INT NOT NULL,
                               waiting_num BIGINT NOT NULL,
                               expired_at TIMESTAMP(6) NOT NULL,
                               created_by BIGINT NOT NULL DEFAULT 0,
                               created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               updated_by BIGINT NOT NULL DEFAULT 0,
                               updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                               deleted_by BIGINT,
                               deleted_at TIMESTAMP(6),
                               CONSTRAINT uq_token UNIQUE (token)
);

CREATE INDEX idx_user_id ON waiting_queue(user_id);
CREATE INDEX idx_status ON waiting_queue(status);
CREATE INDEX idx_expired_at ON waiting_queue(expired_at);
