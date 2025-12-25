package com.example.gongnamul_project.exam;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // 점수 높은 순 → 시간이 짧은 순 → 최근 순으로 상위 10개
    List<ExamResult> findTop10ByOrderByScoreDescElapsedSecondsAscTakenAtDesc();
}
