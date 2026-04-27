package com.job.portal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.job.portal.model.User;
import com.job.portal.model.enums.Role;

class SimpleValidationTests {

    @Test
    void testUserRoleAssignment() {
        User user = new User();
        user.setRole(Role.STUDENT);
        assertEquals(Role.STUDENT, user.getRole(), "Role should be STUDENT");
    }

    @Test
    void testBasicLogic() {
        String status = "ACTIVE";
        assertNotNull(status, "Status should not be null");
    }
}
