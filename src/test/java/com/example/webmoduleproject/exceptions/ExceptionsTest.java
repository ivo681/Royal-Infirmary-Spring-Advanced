package com.example.webmoduleproject.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class ExceptionsTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void ErrorNotFoundAssertionThrow() {
        assertThrows(Exception.class, () -> {
            throw new NotFoundError("Object is not found");
        });
    }

    @Test
    public void DocumentExtractDetailErrorAssertionThrow() {
        assertThrows(Exception.class, () -> {
            throw new DocumentExtractDetailError("Could not extract documents");
        });
    }

    @Test
    public void PermissionErrorAssertionThrow() {
        assertThrows(Exception.class, () -> {
            throw new PermissionError("Permission is denied");
        });
    }


    @Test
    public void BaseExceptionAssertionThrow() {
        assertThrows(BaseAppException.class, () -> {
            throw new NotFoundError("Object is not found");
        });
    }
}
