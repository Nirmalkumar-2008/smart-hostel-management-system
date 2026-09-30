package com.hostel.controller;

import com.hostel.model.*;
import com.hostel.model.enums.*;
import com.hostel.repository.UserRepository;
import com.hostel.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final RoomService roomService;
    private final LeaveService leaveService;
    private final ComplaintService complaintService;
    private final MedicalService medicalService;
    private final FeeService feeService;
    private final NoticeService noticeService;
    private final NotificationService notificationService;
    private final LostFoundService lostFoundService;
    private final MessService messService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentService studentService, RoomService roomService, LeaveService leaveService, ComplaintService complaintService, MedicalService medicalService, FeeService feeService, NoticeService noticeService, NotificationService notificationService, LostFoundService lostFoundService, MessService messService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.roomService = roomService;
        this.leaveService = leaveService;
        this.complaintService = complaintService;
        this.medicalService = medicalService;
        this.feeService = feeService;
        this.noticeService = noticeService;
        this.notificationService = notificationService;
        this.lostFoundService = lostFoundService;
        this.messService = messService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Student getCurrentStudent(Authentication auth) {
        if (auth == null) return null;
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .flatMap(u -> studentService.getStudentByUserId(u.getId()))
                .orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        RoomAllocation activeAllocation = roomService.getActiveAllocationForStudent(student.getId());
        Room allocatedRoom = null;
        if (activeAllocation != null && activeAllocation.getRoomId() != null) {
            try {
                allocatedRoom = roomService.getRoomById(activeAllocation.getRoomId());
            } catch (Exception e) {
                // Room not found fallback
            }
        }
        List<LeaveRequest> myLeaves = leaveService.getLeavesByStudentId(student.getId());
        List<Complaint> myComplaints = complaintService.getComplaintsByStudentId(student.getId());
        List<Fee> myFees = feeService.getFeesByStudentId(student.getId());
        double pendingFeeAmount = myFees != null ? myFees.stream().filter(f -> f.getStatus() != FeeStatus.PAID).mapToDouble(Fee::getRemainingBalance).sum() : 0.0;

        model.addAttribute("student", student);
        model.addAttribute("roomAllocation", activeAllocation);
        model.addAttribute("allocatedRoom", allocatedRoom);
        model.addAttribute("myLeavesCount", myLeaves != null ? myLeaves.size() : 0);
        model.addAttribute("myComplaintsCount", myComplaints != null ? myComplaints.size() : 0);
        model.addAttribute("pendingFeeAmount", pendingFeeAmount);
        model.addAttribute("recentLeaves", myLeaves);
        model.addAttribute("recentComplaints", myComplaints);
        model.addAttribute("recentNotices", noticeService.getActiveNotices());
        model.addAttribute("notifications", notificationService.getUserNotifications(student.getUserId()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(student.getUserId()));
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        User user = (student.getUserId() != null) ? userRepository.findById(student.getUserId()).orElse(null) : null;
        RoomAllocation roomAlloc = roomService.getActiveAllocationForStudent(student.getId());
        Room room = null;
        if (roomAlloc != null && roomAlloc.getRoomId() != null) {
            try {
                room = roomService.getRoomById(roomAlloc.getRoomId());
            } catch (Exception ignored) {}
        }

        List<LeaveRequest> leaves = leaveService.getLeavesByStudentId(student.getId());
        long totalLeaves = leaves != null ? leaves.size() : 0;
        long approvedLeaves = leaves != null ? leaves.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count() : 0;
        long pendingLeaves = leaves != null ? leaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count() : 0;

        List<Complaint> complaints = complaintService.getComplaintsByStudentId(student.getId());
        long totalComplaints = complaints != null ? complaints.size() : 0;
        long resolvedComplaints = complaints != null ? complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED).count() : 0;
        long openComplaints = complaints != null ? complaints.stream().filter(c -> c.getStatus() != ComplaintStatus.RESOLVED).count() : 0;

        int medicalCount = medicalService.getMedicalRequestsByStudentId(student.getId()).size();
        int lostFoundCount = lostFoundService.getLostItemsByStudent(student.getId()).size() + lostFoundService.getFoundItemsByStudent(student.getId()).size();

        List<Fee> fees = feeService.getFeesByStudentId(student.getId());
        double totalFeeAmount = fees != null ? fees.stream().mapToDouble(Fee::getTotalAmount).sum() : 0.0;
        double paidFeeAmount = fees != null ? fees.stream().mapToDouble(Fee::getTotalPaidAmount).sum() : 0.0;
        double pendingFeeAmount = fees != null ? fees.stream().filter(f -> f.getStatus() != FeeStatus.PAID).mapToDouble(Fee::getRemainingBalance).sum() : 0.0;

        model.addAttribute("student", student);
        model.addAttribute("user", user);
        model.addAttribute("roomAllocation", roomAlloc);
        model.addAttribute("room", room);
        model.addAttribute("totalLeaves", totalLeaves);
        model.addAttribute("approvedLeaves", approvedLeaves);
        model.addAttribute("pendingLeaves", pendingLeaves);
        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("resolvedComplaints", resolvedComplaints);
        model.addAttribute("openComplaints", openComplaints);
        model.addAttribute("medicalCount", medicalCount);
        model.addAttribute("lostFoundCount", lostFoundCount);
        model.addAttribute("totalFeeAmount", totalFeeAmount);
        model.addAttribute("paidFeeAmount", paidFeeAmount);
        model.addAttribute("pendingFeeAmount", pendingFeeAmount);

        return "student/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication auth, @RequestParam String phone, @RequestParam String email, @RequestParam String emergencyContact, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            studentService.updateStudentProfile(student.getId(), phone, email, emergencyContact);
            ra.addFlashAttribute("successMessage", "Profile contact details updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(Authentication auth, @RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/student/profile";
        }

        try {
            User user = userRepository.findById(student.getUserId()).orElse(null);
            if (user != null) {
                user.setPasswordSalt("BCRYPT");
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                ra.addFlashAttribute("successMessage", "Password changed successfully.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error changing password: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/room")
    public String roomPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        RoomAllocation alloc = roomService.getActiveAllocationForStudent(student.getId());
        Room room = null;
        if (alloc != null) {
            try {
                room = roomService.getRoomById(alloc.getRoomId());
            } catch (Exception ignored) {}
        }

        model.addAttribute("student", student);
        model.addAttribute("allocation", alloc);
        model.addAttribute("room", room);
        model.addAttribute("roommates", roomService.getRoommatesForStudent(student.getId()));
        return "student/room";
    }

    @GetMapping("/leave")
    public String leavePage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        model.addAttribute("leaves", leaveService.getLeavesByStudentId(student.getId()));
        return "student/leave";
    }

    @PostMapping("/leave/apply")
    public String applyLeave(Authentication auth, @RequestParam String startDate, @RequestParam String endDate, @RequestParam String reason, @RequestParam String destination, @RequestParam String emergencyPhone, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            leaveService.applyForLeave(student.getId(), LocalDate.parse(startDate), LocalDate.parse(endDate), reason, destination, emergencyPhone);
            ra.addFlashAttribute("successMessage", "Leave request submitted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/leave";
    }

    @PostMapping("/leave/{id}/cancel")
    public String cancelLeave(Authentication auth, @PathVariable int id, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            leaveService.cancelLeaveRequest(id, student.getId());
            ra.addFlashAttribute("successMessage", "Leave request cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/leave";
    }

    @GetMapping("/complaints")
    public String complaintsPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        model.addAttribute("complaints", complaintService.getComplaintsByStudentId(student.getId()));
        return "student/complaints";
    }

    @PostMapping("/complaints/submit")
    public String submitComplaint(Authentication auth, @RequestParam ComplaintCategory category, @RequestParam String title, @RequestParam String description, @RequestParam String location, @RequestParam int affectedStudentsCount, @RequestParam int severityScore, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            complaintService.submitComplaint(student.getId(), category, title, description, location, affectedStudentsCount, severityScore);
            ra.addFlashAttribute("successMessage", "Complaint submitted. Priority score calculated automatically.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/complaints";
    }

    @GetMapping("/medical")
    public String medicalPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        model.addAttribute("medicalRequests", medicalService.getMedicalRequestsByStudentId(student.getId()));
        return "student/medical";
    }

    @PostMapping("/medical/submit")
    public String submitMedical(Authentication auth, @RequestParam String requestType, @RequestParam String description, @RequestParam MedicalUrgency urgency, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            medicalService.submitMedicalRequest(student.getId(), requestType, description, urgency);
            ra.addFlashAttribute("successMessage", "Medical request submitted.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/medical";
    }

    @GetMapping("/fees")
    public String feesPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        model.addAttribute("fees", feeService.getFeesByStudentId(student.getId()));
        return "student/fees";
    }

    @GetMapping("/notices")
    public String noticesPage(Model model) {
        model.addAttribute("notices", noticeService.getActiveNotices());
        return "student/notices";
    }

    @GetMapping("/lost-found")
    public String lostFoundPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        model.addAttribute("lostItems", lostFoundService.getLostItemsByStudent(student.getId()));
        model.addAttribute("foundItems", lostFoundService.getFoundItemsByStudent(student.getId()));
        model.addAttribute("allMatches", lostFoundService.getAllMatches());
        return "student/lost-found";
    }

    @PostMapping("/lost-found/report-lost")
    public String reportLost(Authentication auth, @RequestParam String itemName, @RequestParam LostFoundCategory category, @RequestParam String description, @RequestParam(required = false) String color, @RequestParam(required = false) String brand, @RequestParam String lastKnownLocation, @RequestParam String lostDate, @RequestParam String contactPhone, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            lostFoundService.reportLostItem(student.getId(), itemName, category, description, color, brand, lastKnownLocation, LocalDate.parse(lostDate), contactPhone);
            ra.addFlashAttribute("successMessage", "Lost item reported. Matching engine active.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/lost-found";
    }

    @PostMapping("/lost-found/report-found")
    public String reportFound(Authentication auth, @RequestParam String itemName, @RequestParam LostFoundCategory category, @RequestParam String description, @RequestParam(required = false) String color, @RequestParam(required = false) String brand, @RequestParam String foundLocation, @RequestParam String foundDate, @RequestParam(required = false) String storageLocation, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            lostFoundService.reportFoundItem(student.getId(), itemName, category, description, color, brand, foundLocation, LocalDate.parse(foundDate), (storageLocation != null && !storageLocation.trim().isEmpty()) ? storageLocation : "Warden Office");
            ra.addFlashAttribute("successMessage", "Found item reported.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/lost-found";
    }

    @GetMapping("/mess")
    public String messPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("student", student);
        model.addAttribute("todayMenus", messService.getTodayPublishedMenus());
        model.addAttribute("weeklyMap", messService.getWeeklyPublishedMenusMap());

        model.addAttribute("todayAttendanceMap", messService.getStudentTodayAttendanceMap(student.getId()));
        model.addAttribute("attendanceHistory", messService.getStudentAttendanceHistory(student.getId()));
        model.addAttribute("timingConfig", messService.getMessTimingConfig());
        return "student/mess";
    }

    @PostMapping("/mess/attendance")
    public String markAttendance(Authentication auth, @RequestParam MealType mealType, RedirectAttributes ra) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        try {
            messService.recordPostMealAttendance(student.getId(), mealType);
            String displayMealName = (mealType != null ? mealType.name().substring(0, 1) + mealType.name().substring(1).toLowerCase() : "Meal");
            ra.addFlashAttribute("successMessage", displayMealName + " attendance recorded successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/mess";
    }

    @GetMapping("/notifications")
    public String notificationsPage(Authentication auth, Model model) {
        Student student = getCurrentStudent(auth);
        if (student == null) return "redirect:/login";

        model.addAttribute("notifications", notificationService.getUserNotifications(student.getUserId()));
        return "student/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable int id) {
        notificationService.markAsRead(id);
        return "redirect:/student/notifications";
    }
}
