package com.hostel.controller.api;

import com.hostel.model.LostFoundMatch;
import com.hostel.model.Student;
import com.hostel.repository.UserRepository;
import com.hostel.service.LostFoundService;
import com.hostel.service.NotificationService;
import com.hostel.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentApiController {

    private final LostFoundService lostFoundService;
    private final NotificationService notificationService;
    private final StudentService studentService;
    private final UserRepository userRepository;

    public StudentApiController(LostFoundService lostFoundService, NotificationService notificationService, StudentService studentService, UserRepository userRepository) {
        this.lostFoundService = lostFoundService;
        this.notificationService = notificationService;
        this.studentService = studentService;
        this.userRepository = userRepository;
    }

    private Student getCurrentStudent(Authentication auth) {
        if (auth == null) return null;
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .flatMap(u -> studentService.getStudentByUserId(u.getId()))
                .orElse(null);
    }

    @GetMapping("/unread-notifications-count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationsCount(Authentication auth) {
        Student student = getCurrentStudent(auth);
        Map<String, Long> response = new HashMap<>();
        if (student != null) {
            response.put("count", notificationService.getUnreadCount(student.getUserId()));
        } else {
            response.put("count", 0L);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lost-found/matches/{lostItemId}")
    public ResponseEntity<List<LostFoundMatch>> getMatchesForLostItem(@PathVariable int lostItemId) {
        return ResponseEntity.ok(lostFoundService.getMatchesForLostItem(lostItemId));
    }
}
