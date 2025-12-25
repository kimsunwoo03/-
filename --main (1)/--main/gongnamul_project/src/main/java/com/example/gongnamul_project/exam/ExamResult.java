package com.example.gongnamul_project.exam;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;         // 이름
    private int score;               // 점수
    private int total;               // 총 문항 수
    private long elapsedSeconds;     // 걸린 시간(초)

    private LocalDateTime takenAt;   // 응시 시각
}
