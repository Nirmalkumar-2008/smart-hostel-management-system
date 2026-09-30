package com.hostel.repository;

import com.hostel.model.LostFoundMatch;
import com.hostel.model.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LostFoundMatchRepository extends JpaRepository<LostFoundMatch, Integer> {
    List<LostFoundMatch> findByLostItemIdOrderBySimilarityScoreDesc(Integer lostItemId);
    List<LostFoundMatch> findByFoundItemIdOrderBySimilarityScoreDesc(Integer foundItemId);
    Optional<LostFoundMatch> findByLostItemIdAndFoundItemId(Integer lostItemId, Integer foundItemId);
    List<LostFoundMatch> findByStatus(MatchStatus status);
    List<LostFoundMatch> findAllByOrderBySimilarityScoreDesc();
}
