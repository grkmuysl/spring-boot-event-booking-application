CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    seat_count INTEGER NOT NULL,
    event_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    reserved_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_reservation_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_reservation_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT chk_reservation_seat_count CHECK (seat_count > 0),
    CONSTRAINT chk_reservation_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'))
);

CREATE INDEX idx_reservation_status_expires ON reservation (status, expires_at);
CREATE INDEX idx_reservation_created_by ON reservation (created_by);