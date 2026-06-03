package com.doodle.slot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findAllByCalendarIdOrderByStartTimeAsc(Long calendarId);

    Optional<Slot> findByMeetingId(Long meetingId);

    @Query("""
            SELECT s FROM Slot s
            WHERE s.calendar.id = :calendarId
              AND s.startTime < :to
              AND s.endTime > :from
            ORDER BY s.startTime ASC
            """)
    List<Slot> findByCalendarIdAndTimeRange(
            @Param("calendarId") Long calendarId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            SELECT s FROM Slot s
            WHERE s.calendar.id = :calendarId
              AND s.startTime < :to
              AND s.endTime > :from
              AND s.status = :status
            ORDER BY s.startTime ASC
            """)
    List<Slot> findByCalendarIdAndTimeRangeAndStatus(
            @Param("calendarId") Long calendarId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") SlotStatus status
    );

    @Query("""
            SELECT COUNT(s) > 0 FROM Slot s
            WHERE s.calendar.id = :calendarId
              AND s.startTime < :endTime
              AND s.endTime > :startTime
              AND (:excludeId IS NULL OR s.id <> :excludeId)
            """)
    boolean existsOverlapping(
            @Param("calendarId") Long calendarId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId
    );
}
