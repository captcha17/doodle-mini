package com.doodle.user;

import com.doodle.calendar.Calendar;
import com.doodle.calendar.CalendarRepository;
import com.doodle.common.exception.ConflictException;
import com.doodle.common.exception.ResourceNotFoundException;
import com.doodle.user.dto.CreateUserRequest;
import com.doodle.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("User with email " + request.email() + " already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());

        userRepository.save(user);

        Calendar calendar = new Calendar();
        calendar.setUser(user);
        calendar.setTimezone("UTC");
        calendarRepository.save(calendar);

        return UserResponse.from(user, calendar.getId());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> ResourceNotFoundException.of("User", userId));

        Long calendarId = calendarRepository.findByUserId(userId).map(Calendar::getId).orElse(null);

        return UserResponse.from(user, calendarId);
    }
}
