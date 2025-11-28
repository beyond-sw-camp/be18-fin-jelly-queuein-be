CREATE TABLE `outbox` (
                          outbox_id BINARY(16) NOT NULL,
                          event_type VARCHAR(100) NOT NULL,
                          payload JSON NOT NULL,
                          is_published BOOLEAN NOT NULL DEFAULT FALSE,
                          aggregate_id BIGINT NOT NULL,
                          aggregate_type VARCHAR(100) NOT NULL,
                          created_at TIMESTAMP(6) NOT NULL,

                          PRIMARY KEY (outbox_id),
                          INDEX idx_outbox_published (is_published),
                          INDEX idx_outbox_aggregate (aggregate_id, aggregate_type)
);
