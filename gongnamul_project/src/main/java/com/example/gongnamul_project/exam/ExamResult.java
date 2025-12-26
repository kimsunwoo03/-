package com.example.gongnamul_project.exam;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {

    @Id
    @GeneratedValue
    private Long id;          // 시험 결과 ID (PK)

    @Column(nullable = false)
    private String username;  // 시험 본 사람 이름 (예: "현수")

    @Column(nullable = false)
    private int score;        // 점수 (예: 80)

    @Column(nullable = false)
    private int total;        // 총점 (예: 100)

    @Column(nullable = false)
    private LocalDateTime takenAt; // 시험 본 시간
}
