package com.hostel.service;

import com.hostel.algorithm.LostFoundMatchingService;
import com.hostel.exception.HostelException;
import com.hostel.model.FoundItem;
import com.hostel.model.LostFoundMatch;
import com.hostel.model.LostItem;
import com.hostel.model.enums.*;
import com.hostel.repository.FoundItemRepository;
import com.hostel.repository.LostFoundMatchRepository;
import com.hostel.repository.LostItemRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LostFoundService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final LostFoundMatchRepository matchRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;
    private final LostFoundMatchingService matchingService;

    public LostFoundService(LostItemRepository lostItemRepository, FoundItemRepository foundItemRepository, LostFoundMatchRepository matchRepository, StudentRepository studentRepository, NotificationService notificationService, LostFoundMatchingService matchingService) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.matchRepository = matchRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
        this.matchingService = matchingService;
    }

    @Transactional
    public LostItem reportLostItem(int studentId, String name, LostFoundCategory cat, String desc, String color, String brand, String location, LocalDate lostDate, String phone) {
        LostItem lost = new LostItem(null, studentId, name, cat, desc, color, brand, location, lostDate, phone, LostItemStatus.OPEN);
        lost = lostItemRepository.save(lost);
        triggerAutoMatchingForLost(lost);
        return lost;
    }

    @Transactional
    public FoundItem reportFoundItem(int studentId, String name, LostFoundCategory cat, String desc, String color, String brand, String location, LocalDate foundDate, String storage) {
        FoundItem found = new FoundItem(null, studentId, name, cat, desc, color, brand, location, foundDate, storage, FoundItemStatus.OPEN);
        found = foundItemRepository.save(found);
        triggerAutoMatchingForFound(found);
        return found;
    }

    @Transactional
    public void triggerAutoMatchingForLost(LostItem lost) {
        List<FoundItem> openFound = foundItemRepository.findByStatus(FoundItemStatus.OPEN);
        for (FoundItem f : openFound) {
            double score = matchingService.calculateSimilarityScore(lost, f);
            if (score >= 50.0) {
                LostFoundMatch match = matchRepository.findByLostItemIdAndFoundItemId(lost.getId(), f.getId())
                        .orElse(new LostFoundMatch(null, lost.getId(), f.getId(), score, MatchStatus.POSSIBLE, "Auto-generated match score: " + String.format("%.1f%%", score)));

                match.setSimilarityScore(score);
                matchRepository.save(match);

                lost.setStatus(LostItemStatus.POSSIBLE_MATCH);
                lostItemRepository.save(lost);

                f.setStatus(FoundItemStatus.POSSIBLE_MATCH);
                foundItemRepository.save(f);

                studentRepository.findById(lost.getStudentId()).ifPresent(student -> {
                    notificationService.sendNotification(student.getUserId(), "Lost & Found Match Alert",
                            "A possible match (" + String.format("%.1f%%", score) + ") was found for your lost item: " + lost.getItemName(),
                            NotificationType.LOST_FOUND);
                });
            }
        }
    }

    @Transactional
    public void triggerAutoMatchingForFound(FoundItem found) {
        List<LostItem> openLost = lostItemRepository.findByStatus(LostItemStatus.OPEN);
        for (LostItem l : openLost) {
            double score = matchingService.calculateSimilarityScore(l, found);
            if (score >= 50.0) {
                LostFoundMatch match = matchRepository.findByLostItemIdAndFoundItemId(l.getId(), found.getId())
                        .orElse(new LostFoundMatch(null, l.getId(), found.getId(), score, MatchStatus.POSSIBLE, "Auto-generated match score: " + String.format("%.1f%%", score)));

                match.setSimilarityScore(score);
                matchRepository.save(match);

                l.setStatus(LostItemStatus.POSSIBLE_MATCH);
                lostItemRepository.save(l);

                found.setStatus(FoundItemStatus.POSSIBLE_MATCH);
                foundItemRepository.save(found);

                studentRepository.findById(l.getStudentId()).ifPresent(student -> {
                    notificationService.sendNotification(student.getUserId(), "Lost & Found Match Alert",
                            "A possible match (" + String.format("%.1f%%", score) + ") was found for your lost item: " + l.getItemName(),
                            NotificationType.LOST_FOUND);
                });
            }
        }
    }

    @Transactional(readOnly = true)
    public List<LostFoundMatch> getMatchesForLostItem(int lostItemId) {
        List<LostFoundMatch> matches = matchRepository.findByLostItemIdOrderBySimilarityScoreDesc(lostItemId);
        populateMatchDetails(matches);
        return matches;
    }

    @Transactional(readOnly = true)
    public List<LostFoundMatch> getAllMatches() {
        List<LostFoundMatch> matches = matchRepository.findAllByOrderBySimilarityScoreDesc();
        populateMatchDetails(matches);
        return matches;
    }

    private void populateMatchDetails(List<LostFoundMatch> matches) {
        for (LostFoundMatch m : matches) {
            lostItemRepository.findById(m.getLostItemId()).ifPresent(l -> {
                m.setLostItemName(l.getItemName());
                studentRepository.findById(l.getStudentId()).ifPresent(s -> m.setLostStudentName(s.getFullName()));
            });
            foundItemRepository.findById(m.getFoundItemId()).ifPresent(f -> m.setFoundItemName(f.getItemName()));
        }
    }

    @Transactional
    public boolean verifyMatch(int matchId, MatchStatus newStatus, int adminId, String notes) throws HostelException {
        LostFoundMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new HostelException("Match ID " + matchId + " not found."));

        match.setStatus(newStatus);
        match.setVerifiedBy(adminId);
        if (notes != null && !notes.trim().isEmpty()) match.setNotes(notes.trim());
        matchRepository.save(match);

        if (newStatus == MatchStatus.VERIFIED || newStatus == MatchStatus.RESOLVED) {
            lostItemRepository.findById(match.getLostItemId()).ifPresent(l -> {
                l.setStatus(newStatus == MatchStatus.RESOLVED ? LostItemStatus.RETURNED : LostItemStatus.CLAIMED);
                lostItemRepository.save(l);
            });
            foundItemRepository.findById(match.getFoundItemId()).ifPresent(f -> {
                f.setStatus(newStatus == MatchStatus.RESOLVED ? FoundItemStatus.RETURNED : FoundItemStatus.VERIFIED);
                foundItemRepository.save(f);
            });
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<LostItem> getLostItemsByStudent(int studentId) {
        return lostItemRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public List<FoundItem> getFoundItemsByStudent(int studentId) {
        return foundItemRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Transactional(readOnly = true)
    public List<LostItem> getAllLostItems() {
        List<LostItem> list = lostItemRepository.findAllByOrderByCreatedAtDesc();
        for (LostItem l : list) {
            studentRepository.findById(l.getStudentId()).ifPresent(s -> l.setStudentName(s.getFullName()));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<FoundItem> getAllFoundItems() {
        List<FoundItem> list = foundItemRepository.findAllByOrderByCreatedAtDesc();
        for (FoundItem f : list) {
            studentRepository.findById(f.getStudentId()).ifPresent(s -> f.setStudentName(s.getFullName()));
        }
        return list;
    }
}
