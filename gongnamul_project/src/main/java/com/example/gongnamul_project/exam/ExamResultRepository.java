package com.example.gongnamul_project.exam;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // 점수 높은 순으로 상위 10개 가져오기 (랭킹용)
    List<ExamResult> findTop10ByOrderByScoreDesc();
}
