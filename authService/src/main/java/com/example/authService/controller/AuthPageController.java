package com.example.authService.controller;

import com.example.authService.dto.LoginRequest;
import com.example.authService.dto.UserRegistrationRequest;
import com.example.authService.entity.EntityUsers;
import com.example.authService.repository.UserRepository;
import com.example.authService.service.RefreshTokenService;
import com.example.authService.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userRegistrationRequest", new UserRegistrationRequest());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        String userId = (String) auth.getPrincipal();

        EntityUsers user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("userName", user.getUserName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("registrationDate", user.getRegistrationDate());

        return "profile";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("userRegistrationRequest") UserRegistrationRequest request,
                                  BindingResult bindingResult,
                                  HttpServletResponse response,
                                  Model model) {
        // ★ Валидация
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldErrors().stream()
                    .map(f -> f.getDefaultMessage())
                    .findFirst()
                    .orElse("Ошибка валидации");
            model.addAttribute("error", error);
            return "register";
        }

        try {
            userService.registeredUser(request);

            // Сразу логиним — получаем [access, refresh]
            String[] tokens = userService.loginUser(request.getEmail(), request.getPassword());

            // Access token — 15 минут
            Cookie accessCookie = new Cookie("access_token", tokens[0]);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(900);
            response.addCookie(accessCookie);

            // Refresh token — 7 дней
            Cookie refreshCookie = new Cookie("refresh_token", tokens[1]);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(604800);
            response.addCookie(refreshCookie);

            return "redirect:/profile";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest request,
                               BindingResult bindingResult,
                               HttpServletResponse response,
                               Model model) {
        // ★ Валидация
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldErrors().stream()
                    .map(f -> f.getDefaultMessage())
                    .findFirst()
                    .orElse("Ошибка валидации");
            model.addAttribute("error", error);
            return "login";
        }

        try {
            String[] tokens = userService.loginUser(request.getEmail(), request.getPassword());

            Cookie accessCookie = new Cookie("access_token", tokens[0]);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(900);
            response.addCookie(accessCookie);

            Cookie refreshCookie = new Cookie("refresh_token", tokens[1]);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(604800);
            response.addCookie(refreshCookie);

            return "redirect:/profile";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userId = (String) auth.getPrincipal();
            refreshTokenService.delete(userId);
        }

        Cookie accessCookie = new Cookie("access_token", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return "redirect:/";
    }
}