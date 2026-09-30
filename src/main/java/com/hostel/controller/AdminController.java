package com.hostel.controller;

import com.hostel.model.*;
import com.hostel.model.enums.*;
import com.hostel.service.*;
import com.hostel.algorithm.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hostel.repository.AdminRepository;
import com.hostel.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
    private final FoodWasteService foodWasteService;
    private final AnalyticsService analyticsService;
    private final RoomRecommendationService roomRecommendationService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(StudentService studentService, RoomService roomService, LeaveService leaveService, ComplaintService complaintService, MedicalService medicalService, FeeService feeService, NoticeService noticeService, NotificationService notificationService, LostFoundService lostFoundService, MessService messService, FoodWasteService foodWasteService, AnalyticsService analyticsService, RoomRecommendationService roomRecommendationService, AdminRepository adminRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
        this.foodWasteService = foodWasteService;
        this.analyticsService = analyticsService;
        this.roomRecommendationService = roomRecommendationService;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Admin getCurrentAdmin(Authentication auth) {
        if (auth != null) {
            String username = auth.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                Optional<Admin> adminOpt = adminRepository.findByUserId(userOpt.get().getId());
                if (adminOpt.isPresent()) return adminOpt.get();
            }
        }
        List<Admin> admins = adminRepository.findAll();
        if (!admins.isEmpty()) return admins.get(0);
        return new Admin(1, 1, "Chief Administrator", "admin@smarthostel.edu", "+91 98765 43210", "Hostel Administration");
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AnalyticsService.SystemDashboardStats stats = analyticsService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("totalStudents", stats.getTotalStudents());
        model.addAttribute("totalRooms", stats.getTotalRooms());
        model.addAttribute("occupiedRooms", stats.getOccupiedRooms());
        model.addAttribute("availableRooms", stats.getAvailableRooms());
        model.addAttribute("pendingLeaves", stats.getPendingLeaves());
        model.addAttribute("openComplaints", stats.getOpenComplaints());
        model.addAttribute("recentLeaves", leaveService.getAllLeaves());
        model.addAttribute("recentComplaints", complaintService.getAllComplaints());
        model.addAttribute("recentWaste", foodWasteService.getAllFoodWasteRecords());
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String studentsPage(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status,
            Model model) {
        List<Student> students = studentService.searchAndFilterStudents(query, course, year, status);
        List<String> courses = studentService.getAllStudents().stream()
                .map(Student::getCourse)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("students", students);
        model.addAttribute("query", query);
        model.addAttribute("course", course);
        model.addAttribute("year", year);
        model.addAttribute("status", status);
        model.addAttribute("courses", courses);
        return "admin/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String username, @RequestParam String password, @RequestParam String studentCode, @RequestParam String fullName, @RequestParam String email, @RequestParam String phone, @RequestParam String gender, @RequestParam String course, @RequestParam int yearOfStudy, @RequestParam String emergencyContact, RedirectAttributes ra) {
        try {
            studentService.createStudent(username, password, studentCode, fullName, email, phone, gender, course, yearOfStudy, emergencyContact);
            ra.addFlashAttribute("successMessage", "Student " + fullName + " registered successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error registering student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/edit")
    public String editStudent(
            @PathVariable int id,
            @RequestParam String studentCode,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String gender,
            @RequestParam String course,
            @RequestParam int yearOfStudy,
            @RequestParam String emergencyContact,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            RedirectAttributes ra) {
        try {
            studentService.updateStudentDetails(id, studentCode, fullName, email, phone, gender, course, yearOfStudy, emergencyContact, newPassword, confirmPassword);
            ra.addFlashAttribute("successMessage", "Student details updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/status")
    public String toggleStudentStatus(@PathVariable int id, @RequestParam String status, RedirectAttributes ra) {
        try {
            studentService.updateStudentStatus(id, status);
            ra.addFlashAttribute("successMessage", "Student status updated to " + status);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable int id, RedirectAttributes ra) {
        try {
            studentService.deleteStudent(id);
            ra.addFlashAttribute("successMessage", "Student deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/rooms")
    public String roomsPage(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String status,
            Model model) {
        List<Room> rooms = roomService.searchAndFilterRooms(query, floor, roomType, status);
        List<Room> allRooms = roomService.getAllRooms();

        List<Integer> floors = allRooms.stream()
                .map(Room::getFloor)
                .filter(f -> f != null)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());

        AnalyticsService.SystemDashboardStats stats = analyticsService.getDashboardStats();

        model.addAttribute("rooms", rooms);
        model.addAttribute("allRoomsCount", allRooms.size());
        model.addAttribute("stats", stats);
        model.addAttribute("query", query);
        model.addAttribute("floor", floor);
        model.addAttribute("roomType", roomType);
        model.addAttribute("status", status);
        model.addAttribute("floors", floors);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/rooms";
    }

    @PostMapping("/rooms/add")
    public String addRoom(
            @RequestParam String roomNumber,
            @RequestParam String block,
            @RequestParam int floor,
            @RequestParam int capacity,
            @RequestParam(defaultValue = "0") int currentOccupancy,
            @RequestParam RoomType roomType,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam double rent,
            @RequestParam(required = false) String description,
            RedirectAttributes ra) {
        try {
            roomService.addRoom(roomNumber, block, floor, capacity, currentOccupancy, roomType, status, rent, description);
            ra.addFlashAttribute("successMessage", "Room " + roomNumber + " added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error adding room: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/{id}/edit")
    public String editRoom(
            @PathVariable int id,
            @RequestParam String roomNumber,
            @RequestParam String block,
            @RequestParam int floor,
            @RequestParam int capacity,
            @RequestParam int currentOccupancy,
            @RequestParam RoomType roomType,
            @RequestParam RoomStatus status,
            @RequestParam double rent,
            @RequestParam(required = false) String description,
            RedirectAttributes ra) {
        try {
            roomService.updateRoom(id, roomNumber, block, floor, capacity, currentOccupancy, roomType, status, rent, description);
            ra.addFlashAttribute("successMessage", "Room " + roomNumber + " updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable int id, RedirectAttributes ra) {
        try {
            roomService.deleteRoom(id);
            ra.addFlashAttribute("successMessage", "Room deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/allocate")
    public String allocateRoom(@RequestParam int studentId, @RequestParam int roomId, @RequestParam(required = false) String notes, RedirectAttributes ra) {
        try {
            roomService.allocateRoom(studentId, roomId, notes);
            ra.addFlashAttribute("successMessage", "Room allocated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Allocation failed: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/deallocate")
    public String deallocateStudent(@RequestParam int studentId, RedirectAttributes ra) {
        try {
            roomService.deallocateStudent(studentId);
            ra.addFlashAttribute("successMessage", "Student deallocated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Deallocation failed: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @GetMapping("/allocations")
    public String allocationsPage(Model model) {
        model.addAttribute("allocations", roomService.getAllAllocations());
        return "admin/allocations";
    }

    @GetMapping("/waiting-list")
    public String waitingListPage(Model model) {
        model.addAttribute("waitingList", roomService.getWaitingList());
        return "admin/waiting-list";
    }

    @GetMapping("/leaves")
    public String leavesPage(Model model) {
        model.addAttribute("leaves", leaveService.getAllLeaves());
        return "admin/leaves";
    }

    @PostMapping("/leaves/{id}/review")
    public String reviewLeave(@PathVariable int id, @RequestParam LeaveStatus status, @RequestParam(required = false) String adminRemarks, RedirectAttributes ra) {
        try {
            leaveService.reviewLeaveRequest(id, status, adminRemarks);
            ra.addFlashAttribute("successMessage", "Leave request " + status.name().toLowerCase() + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/leaves";
    }

    @GetMapping("/complaints")
    public String complaintsPage(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin/complaints";
    }

    @PostMapping("/complaints/{id}/status")
    public String updateComplaintStatus(@PathVariable int id, @RequestParam ComplaintStatus status, @RequestParam(required = false) String resolutionNotes, RedirectAttributes ra) {
        try {
            complaintService.updateComplaintStatus(id, status, resolutionNotes);
            ra.addFlashAttribute("successMessage", "Complaint status updated to " + status.name());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    @GetMapping("/medical")
    public String medicalPage(Model model) {
        model.addAttribute("medicalRequests", medicalService.getAllMedicalRequests());
        return "admin/medical";
    }

    @PostMapping("/medical/{id}/update")
    public String updateMedicalStatus(@PathVariable int id, @RequestParam MedicalStatus status, @RequestParam(required = false) String doctorAssigned, @RequestParam(required = false) String doctorNotes, RedirectAttributes ra) {
        try {
            medicalService.updateMedicalStatus(id, status, doctorAssigned, doctorNotes);
            ra.addFlashAttribute("successMessage", "Medical request status updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/medical";
    }

    @GetMapping("/fees")
    public String feesPage(Model model) {
        model.addAttribute("fees", feeService.getAllFees());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/fees";
    }

    @PostMapping("/fees/create")
    public String createFee(@RequestParam String feeCode, @RequestParam int studentId, @RequestParam FeeType feeType, @RequestParam double amount, @RequestParam String dueDate, @RequestParam String academicTerm, RedirectAttributes ra) {
        try {
            feeService.createFeeRecord(feeCode, studentId, feeType, amount, LocalDate.parse(dueDate), academicTerm);
            ra.addFlashAttribute("successMessage", "Fee record created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error creating fee: " + e.getMessage());
        }
        return "redirect:/admin/fees";
    }

    @PostMapping("/fees/payment")
    public String recordPayment(@RequestParam int feeId, @RequestParam double amountPaid, @RequestParam PaymentMethod method, @RequestParam String transactionReference, @RequestParam(required = false) String notes, RedirectAttributes ra) {
        try {
            feeService.recordPayment(feeId, amountPaid, method, transactionReference, notes);
            ra.addFlashAttribute("successMessage", "Payment of $" + amountPaid + " recorded.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/fees";
    }

    @GetMapping("/notices")
    public String noticesPage(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "admin/notices";
    }

    @PostMapping("/notices/create")
    public String createNotice(@RequestParam String title, 
                               @RequestParam String content, 
                               @RequestParam NoticeCategory category, 
                               @RequestParam(required = false) String expiryDate, 
                               RedirectAttributes ra) {
        try {
            LocalDate expDate;
            if (expiryDate == null || expiryDate.trim().isEmpty()) {
                expDate = LocalDate.now().plusDays(30);
            } else {
                expDate = LocalDate.parse(expiryDate.trim());
            }
            noticeService.createNotice(title, content, category, 1, expDate);
            ra.addFlashAttribute("successMessage", "Notice published successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to publish notice: " + e.getMessage());
        }
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/{id}/deactivate")
    public String deactivateNotice(@PathVariable int id, RedirectAttributes ra) {
        noticeService.deactivateNotice(id);
        ra.addFlashAttribute("successMessage", "Notice deactivated successfully.");
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable int id, RedirectAttributes ra) {
        noticeService.deleteNotice(id);
        ra.addFlashAttribute("successMessage", "Notice deleted successfully.");
        return "redirect:/admin/notices";
    }

    @GetMapping("/lost-found")
    public String lostFoundPage(Model model) {
        model.addAttribute("lostItems", lostFoundService.getAllLostItems());
        model.addAttribute("foundItems", lostFoundService.getAllFoundItems());
        model.addAttribute("matches", lostFoundService.getAllMatches());
        return "admin/lost-found";
    }

    @PostMapping("/lost-found/verify")
    public String verifyMatch(@RequestParam int matchId, @RequestParam MatchStatus status, @RequestParam(required = false) String notes, RedirectAttributes ra) {
        try {
            lostFoundService.verifyMatch(matchId, status, 1, notes);
            ra.addFlashAttribute("successMessage", "Match verified and status set to " + status);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/lost-found";
    }

    @GetMapping("/mess")
    public String messPage(@RequestParam(required = false) String date,
                           @RequestParam(required = false) MealType mealType,
                           @RequestParam(required = false) MessMenuStatus status,
                           @RequestParam(required = false) String predDate,
                           @RequestParam(required = false) MealType predMeal,
                           Model model) {
        LocalDate filterDate = (date != null && !date.trim().isEmpty()) ? LocalDate.parse(date.trim()) : null;
        LocalDate predictionTargetDate = (predDate != null && !predDate.trim().isEmpty()) ? LocalDate.parse(predDate.trim()) : LocalDate.now().plusDays(1);
        MealType predictionTargetMeal = (predMeal != null) ? predMeal : MealType.LUNCH;

        long totalStudents = messService.getTotalStudentsCount();
        long breakfastAttendance = messService.getTodayAttendanceCount(MealType.BREAKFAST);
        long lunchAttendance = messService.getTodayAttendanceCount(MealType.LUNCH);
        long dinnerAttendance = messService.getTodayAttendanceCount(MealType.DINNER);

        model.addAttribute("todayMealCount", messService.getTodayMealCount());
        model.addAttribute("publishedMenuCount", messService.getPublishedMenuCount());
        model.addAttribute("upcomingMenuCount", messService.getUpcomingMenuCount());
        model.addAttribute("todayStatusText", messService.getTodayStatusText());

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("breakfastAttendance", breakfastAttendance);
        model.addAttribute("lunchAttendance", lunchAttendance);
        model.addAttribute("dinnerAttendance", dinnerAttendance);

        model.addAttribute("breakfastPercentage", messService.calculateAttendancePercentage(breakfastAttendance, totalStudents));
        model.addAttribute("lunchPercentage", messService.calculateAttendancePercentage(lunchAttendance, totalStudents));
        model.addAttribute("dinnerPercentage", messService.calculateAttendancePercentage(dinnerAttendance, totalStudents));

        model.addAttribute("menus", messService.filterMenus(filterDate, mealType, status));
        model.addAttribute("filterDate", filterDate);
        model.addAttribute("filterMeal", mealType);
        model.addAttribute("filterStatus", status);

        // Prediction & Historical Log
        model.addAttribute("predDate", predictionTargetDate);
        model.addAttribute("predMeal", predictionTargetMeal);
        model.addAttribute("prediction", messService.generateMealPrediction(predictionTargetDate, predictionTargetMeal));
        model.addAttribute("attendanceLog", messService.getFilteredAttendance(filterDate, mealType));

        return "admin/mess";
    }

    @PostMapping("/mess/add")
    public String addMessMenu(@RequestParam String menuDate,
                              @RequestParam MealType mealType,
                              @RequestParam String startTime,
                              @RequestParam String endTime,
                              @RequestParam List<String> items,
                              @RequestParam(required = false) String description,
                              @RequestParam MessMenuStatus status,
                              RedirectAttributes ra) {
        try {
            LocalDate parsedDate = LocalDate.parse(menuDate);
            messService.createMenu(parsedDate, mealType, startTime, endTime, items, description, status);
            ra.addFlashAttribute("successMessage", "Mess menu created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/mess";
    }

    @PostMapping("/mess/{id}/edit")
    public String editMessMenu(@PathVariable int id,
                               @RequestParam String menuDate,
                               @RequestParam MealType mealType,
                               @RequestParam String startTime,
                               @RequestParam String endTime,
                               @RequestParam List<String> items,
                               @RequestParam(required = false) String description,
                               @RequestParam MessMenuStatus status,
                               RedirectAttributes ra) {
        try {
            LocalDate parsedDate = LocalDate.parse(menuDate);
            messService.updateMenu(id, parsedDate, mealType, startTime, endTime, items, description, status);
            ra.addFlashAttribute("successMessage", "Menu updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/mess";
    }

    @PostMapping("/mess/{id}/delete")
    public String deleteMessMenu(@PathVariable int id, RedirectAttributes ra) {
        try {
            messService.deleteMenu(id);
            ra.addFlashAttribute("successMessage", "Menu deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/mess";
    }

    @GetMapping("/food-waste")
    public String foodWastePage(Model model) {
        model.addAttribute("records", foodWasteService.getAllFoodWasteRecords());
        return "admin/food-waste";
    }

    @PostMapping("/food-waste/record")
    public String recordFoodWaste(@RequestParam String date, @RequestParam MealType mealType, @RequestParam int prepared, @RequestParam int expected, @RequestParam int actual, @RequestParam(required = false) String notes, RedirectAttributes ra) {
        try {
            foodWasteService.recordFoodWaste(LocalDate.parse(date), mealType, prepared, expected, actual, notes, 1);
            ra.addFlashAttribute("successMessage", "Food waste metrics recorded.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/food-waste";
    }

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("stats", analyticsService.getDashboardStats());
        return "admin/reports";
    }

    @GetMapping("/notifications")
    public String notificationsPage(Model model) {
        model.addAttribute("notifications", notificationService.getUserNotifications(1));
        return "admin/notifications";
    }

    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {
        Admin admin = getCurrentAdmin(auth);
        User user = (admin != null && admin.getUserId() != null) ? userRepository.findById(admin.getUserId()).orElse(null) : null;
        AnalyticsService.SystemDashboardStats stats = analyticsService.getDashboardStats();

        model.addAttribute("admin", admin);
        model.addAttribute("user", user);
        model.addAttribute("stats", stats);
        model.addAttribute("totalStudents", stats.getTotalStudents());
        model.addAttribute("totalRooms", stats.getTotalRooms());
        model.addAttribute("occupiedRooms", stats.getOccupiedRooms());
        model.addAttribute("availableRooms", stats.getAvailableRooms());
        model.addAttribute("pendingLeaves", stats.getPendingLeaves());
        model.addAttribute("openComplaints", stats.getOpenComplaints());
        model.addAttribute("medicalRequestsCount", medicalService.getAllMedicalRequests().size());
        model.addAttribute("lostItemsCount", lostFoundService.getAllLostItems().size() + lostFoundService.getAllFoundItems().size());
        model.addAttribute("noticesCount", noticeService.getAllNotices().size());
        model.addAttribute("pendingFeesCount", feeService.getAllFees().stream().filter(f -> f.getStatus() != FeeStatus.PAID).count());
        return "admin/profile";
    }

    @PostMapping("/profile/update")
    public String updateAdminProfile(Authentication auth, @RequestParam String fullName, @RequestParam String email, @RequestParam String phone, @RequestParam String department, RedirectAttributes ra) {
        Admin admin = getCurrentAdmin(auth);
        if (admin != null) {
            admin.setFullName(fullName);
            admin.setEmail(email);
            admin.setPhone(phone);
            admin.setDepartment(department);
            adminRepository.save(admin);
            ra.addFlashAttribute("successMessage", "Admin profile details updated successfully.");
        }
        return "redirect:/admin/profile";
    }

    @PostMapping("/profile/password")
    public String changeAdminPassword(Authentication auth, @RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, RedirectAttributes ra) {
        Admin admin = getCurrentAdmin(auth);
        if (admin == null || admin.getUserId() == null) return "redirect:/admin/profile";

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/admin/profile";
        }

        try {
            User user = userRepository.findById(admin.getUserId()).orElse(null);
            if (user != null) {
                user.setPasswordSalt("BCRYPT");
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                ra.addFlashAttribute("successMessage", "Password changed successfully.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error changing password: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    @GetMapping("/smart-room-allocation")
    public String smartRoomAllocationPage(@RequestParam(required = false) Integer studentId,
                                          @RequestParam(required = false) String block,
                                          @RequestParam(required = false) RoomType roomType,
                                          @RequestParam(required = false) Integer floor,
                                          Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("distinctBlocks", roomService.getDistinctBlocks());

        Student selectedStudent = null;
        RoomAllocation currentAllocation = null;
        Room currentRoom = null;
        List<RoomRecommendationEngine.RecommendationResult> recommendations = new ArrayList<>();

        if (studentId != null) {
            try {
                selectedStudent = studentService.getStudentById(studentId);
                currentAllocation = roomService.getActiveAllocationForStudent(studentId);
                if (currentAllocation != null) {
                    currentRoom = roomService.getRoomById(currentAllocation.getRoomId());
                }

                String prefBlock = (block != null && !block.trim().isEmpty()) ? block.trim() : selectedStudent.getPreferredBlock();
                RoomType prefRoomType = (roomType != null) ? roomType : selectedStudent.getPreferredRoomType();
                Integer prefFloor = (floor != null && floor > 0) ? floor : selectedStudent.getPreferredFloor();

                recommendations = roomRecommendationService.getRankedRecommendations(selectedStudent, prefBlock, prefFloor, prefRoomType);

                model.addAttribute("prefBlock", prefBlock);
                model.addAttribute("prefRoomType", prefRoomType);
                model.addAttribute("prefFloor", prefFloor);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Student not found: " + e.getMessage());
            }
        }

        model.addAttribute("selectedStudent", selectedStudent);
        model.addAttribute("studentId", studentId);
        model.addAttribute("currentAllocation", currentAllocation);
        model.addAttribute("currentRoom", currentRoom);
        model.addAttribute("recommendations", recommendations);
        return "admin/smart-room-allocation";
    }

    @PostMapping("/smart-room-allocation/assign")
    public String assignRecommendedRoom(@RequestParam Integer studentId,
                                        @RequestParam Integer roomId,
                                        @RequestParam(required = false) String notes,
                                        RedirectAttributes ra) {
        try {
            Student student = studentService.getStudentById(studentId);
            Room room = roomService.getRoomById(roomId);

            String allocNotes = (notes != null && !notes.trim().isEmpty()) ? notes.trim() : "Assigned via Smart Room Allocation Engine";
            roomService.allocateRoom(studentId, roomId, allocNotes);

            ra.addFlashAttribute("successMessage", "Room " + room.getRoomNumber() + " (Block " + room.getBlock() + ") successfully assigned to " + student.getFullName() + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Room Allocation Failed: " + e.getMessage());
        }
        return "redirect:/admin/smart-room-allocation?studentId=" + studentId;
    }
}
