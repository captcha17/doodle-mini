package com.doodle.meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("""
            SELECT DISTINCT m FROM Meeting m
            LEFT JOIN FETCH m.participants p
            LEFT JOIN FETCH p.user
            WHERE m.id = :id
            """)
    Optional<Meeting> findByIdWithParticipants(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT m FROM Meeting m
            LEFT JOIN FETCH m.participants p
            LEFT JOIN FETCH p.user
            JOIN Slot s ON s.meeting = m
            WHERE s.calendar.id = :calendarId
            ORDER BY m.createdAt DESC
            """)
    List<Meeting> findAllByCalendarId(@Param("calendarId") Long calendarId);
}
