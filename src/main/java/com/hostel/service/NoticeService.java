package com.hostel.service;

import com.hostel.model.Notice;
import com.hostel.model.enums.NoticeCategory;
import com.hostel.repository.AdminRepository;
import com.hostel.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;

    public NoticeService(NoticeRepository noticeRepository, AdminRepository adminRepository) {
        this.noticeRepository = noticeRepository;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public Notice createNotice(String title, String content, NoticeCategory category, int createdByAdminId, LocalDate expiryDate) {
        Notice notice = new Notice(null, title, content, category, createdByAdminId, LocalDate.now(), expiryDate, true);
        return noticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public List<Notice> getActiveNotices() {
        List<Notice> list = noticeRepository.findByActiveTrueAndExpiryDateGreaterThanEqualOrderByCreatedDateDesc(LocalDate.now());
        for (Notice n : list) {
            adminRepository.findById(n.getCreatedBy()).ifPresent(a -> n.setCreatedByName(a.getFullName()));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<Notice> getAllNotices() {
        List<Notice> list = noticeRepository.findAllByOrderByCreatedDateDesc();
        for (Notice n : list) {
            adminRepository.findById(n.getCreatedBy()).ifPresent(a -> n.setCreatedByName(a.getFullName()));
        }
        return list;
    }

    @Transactional
    public boolean deactivateNotice(int noticeId) {
        return noticeRepository.findById(noticeId).map(n -> {
            n.setActive(false);
            noticeRepository.save(n);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean deleteNotice(int noticeId) {
        return noticeRepository.findById(noticeId).map(n -> {
            noticeRepository.delete(n);
            return true;
        }).orElse(false);
    }
}
