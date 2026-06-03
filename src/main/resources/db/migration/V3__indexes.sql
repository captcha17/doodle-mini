CREATE INDEX idx_slots_calendar_time
    ON slots (calendar_id, start_time, end_time);

CREATE INDEX idx_slots_calendar_status
    ON slots (calendar_id, status);

CREATE INDEX idx_slots_meeting_id
    ON slots (meeting_id)
    WHERE meeting_id IS NOT NULL;

CREATE INDEX idx_meeting_participants_meeting
    ON meeting_participants (meeting_id);

CREATE INDEX idx_meeting_participants_user
    ON meeting_participants (user_id);
