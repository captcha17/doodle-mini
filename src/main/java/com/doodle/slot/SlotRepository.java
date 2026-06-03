package com.doodle.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findAllByCalendarIdOrderByStartTimeAsc(Long calendarId);

    Optional<Slot> findByMeetingId(Long meetingId);
}
