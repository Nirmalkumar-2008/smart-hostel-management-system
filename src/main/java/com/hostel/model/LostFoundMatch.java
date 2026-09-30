package com.hostel.model;

import com.hostel.model.enums.MatchStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_found_matches", uniqueConstraints = {
    @UniqueConstraint(name = "unq_match", columnNames = {"lost_item_id", "found_item_id"})
})
public class LostFoundMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lost_item_id", nullable = false)
    private Integer lostItemId;

    @Column(name = "found_item_id", nullable = false)
    private Integer foundItemId;

    @Column(name = "similarity_score", nullable = false)
    private double similarityScore;

    @Column(name = "matched_date", insertable = false, updatable = false)
    private LocalDateTime matchedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.POSSIBLE;

    @Column(name = "verified_by")
    private Integer verifiedBy;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String lostItemName;
    @Transient
    private String foundItemName;
    @Transient
    private String lostStudentName;

    public LostFoundMatch() {}

    public LostFoundMatch(Integer id, Integer lostItemId, Integer foundItemId, Double similarityScore, MatchStatus status, String notes) {
        this.id = id;
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.similarityScore = similarityScore;
        this.status = status;
        this.notes = notes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getLostItemId() { return lostItemId; }
    public void setLostItemId(Integer lostItemId) { this.lostItemId = lostItemId; }

    public Integer getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Integer foundItemId) { this.foundItemId = foundItemId; }

    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

    public LocalDateTime getMatchedDate() { return matchedDate; }
    public void setMatchedDate(LocalDateTime matchedDate) { this.matchedDate = matchedDate; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Integer getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(Integer verifiedBy) { this.verifiedBy = verifiedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLostItemName() { return lostItemName; }
    public void setLostItemName(String lostItemName) { this.lostItemName = lostItemName; }

    public String getFoundItemName() { return foundItemName; }
    public void setFoundItemName(String foundItemName) { this.foundItemName = foundItemName; }

    public String getLostStudentName() { return lostStudentName; }
    public void setLostStudentName(String lostStudentName) { this.lostStudentName = lostStudentName; }
}
