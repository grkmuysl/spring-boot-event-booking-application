CREATE TABLE events (
	
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    vanue VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    capacity INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT NOT NULL,
    CONSTRAINT fk_events_created_by FOREIGN KEY (created_by) REFERENCES users(id)
    
);