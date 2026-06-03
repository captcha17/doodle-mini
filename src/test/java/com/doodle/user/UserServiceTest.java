package com.doodle.user;

import com.doodle.AbstractIntegrationTest;
import com.doodle.common.exception.ConflictException;
import com.doodle.user.dto.CreateUserRequest;
import com.doodle.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserServiceTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldCreateUserAndCalendar() {
        CreateUserRequest request = new CreateUserRequest("Dima", "dima@doodle.com");

        UserResponse response = userService.createUser(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("dima@doodle.com");
        assertThat(response.name()).isEqualTo("Dima");
        assertThat(response.calendarId()).isNotNull();
    }

    @Test
    void createUser_shouldFailOnDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest("Dima", "dima2@doodle.com");
        userService.createUser(request);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getUser_shouldReturnCorrectUser() {
        CreateUserRequest request = new CreateUserRequest("Alex", "alex@doodle.com");
        UserResponse created = userService.createUser(request);

        UserResponse found = userService.getUserById(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.email()).isEqualTo("alex@doodle.com");
    }
}
