package com.example.authService.service;

import com.example.authService.dto.UserRegistrationRequest;
import com.example.authService.dto.UserResponse;
import com.example.authService.model.Users;
import com.example.authService.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Пользователь с электронной почтой: "
                    + request.getEmail() + " уже существует.");
        }

        Users newUsers = new Users(request.getUserName(), request.getEmail(), request.getPassword());
        Users savedUsers = userRepository.save(newUsers);

        return mapToResponse(savedUsers);

    }

    private UserResponse mapToResponse(Users user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRegistrationDate(user.getRegistrationDate());

        return response;
    }
}
