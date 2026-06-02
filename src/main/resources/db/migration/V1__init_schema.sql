CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE calendars
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    timezone   VARCHAR(100) NOT NULL DEFAULT 'UTC',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE meetings
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE meeting_participants
(
    id         BIGSERIAL PRIMARY KEY,
    meeting_id BIGINT NOT NULL REFERENCES meetings (id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_meeting_participant UNIQUE (meeting_id, user_id)
);

CREATE TABLE slots
(
    id          BIGSERIAL PRIMARY KEY,
    calendar_id BIGINT      NOT NULL REFERENCES calendars (id) ON DELETE CASCADE,
    start_time  TIMESTAMP   NOT NULL,
    end_time    TIMESTAMP   NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    meeting_id  BIGINT      REFERENCES meetings (id) ON DELETE SET NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_slot_times CHECK (end_time > start_time),
    CONSTRAINT chk_slot_status CHECK (status IN ('AVAILABLE', 'BUSY'))
);