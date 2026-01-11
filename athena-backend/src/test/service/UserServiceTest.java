package com.example.demo.service;

import com.example.demo.dto.UserRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService service;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        when(repo.findAll()).thenReturn(List.of(new User()));

        List<User> users = service.getAllUsers();

        assertEquals(1, users.size());
        verify(repo, times(1)).findAll();
    }

}
