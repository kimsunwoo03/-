package com.example.gongnamul_project.exam;

import com.example.gongnamul_project.words.WordRepository;
import com.example.gongnamul_project.words.WordSaving;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final WordRepository wordRepository;
    private final ExamResultRepository examResultRepository;

    // ======================= 문제 20개 제공 ==========================
    @GetMapping("/questions")
    public List<ExamQuestionDto> getQuestions() {
        List<WordSaving> words = wordRepository.findAll();

        if (words.isEmpty()) {
            throw new IllegalStateException("등록된 단어가 없습니다.");
        }

        // 단어 섞기
        Collections.shuffle(words);

        // 최대 20개까지만 사용
        int count = Math.min(20, words.size());
        List<WordSaving> selected = words.subList(0, count);

        List<ExamQuestionDto> questions = new ArrayList<>();

        for (int i = 0; i < selected.size(); i++) {
            WordSaving w = selected.get(i);

            if (i < 10) {
                // 영어 → 뜻 쓰기
                questions.add(new ExamQuestionDto(
                        w.getId(),
                        "MEANING",
                        w.getWord()
                ));
            } else {
                // 뜻 → 영어 쓰기
                questions.add(new ExamQuestionDto(
                        w.getId(),
                        "WORD",
                        w.getMeaning()
                ));
            }
        }

        // 문제 순서 한 번 더 섞기
        Collections.shuffle(questions);
        return questions;
    }

    // ======================= 시험 제출 + 저장 ==========================
    @PostMapping("/submit")
    public ExamResultDto submitExam(@RequestBody ExamSubmitRequest request) {

        // 1) wordId들 한 번에 모아서 조회
        List<Long> ids = request.answers().stream()
                .map(ExamAnswerDto::wordId)
                .distinct()
                .toList();

        Map<Long, WordSaving> wordMap = wordRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(WordSaving::getId, Function.identity()));

        int correctCount = 0;
        List<ExamAnswerResultDto> details = new ArrayList<>();

        // 2) 각 문제 채점
        for (ExamAnswerDto ans : request.answers()) {

            WordSaving word = wordMap.get(ans.wordId());
            if (word == null) continue;

            String user = ans.userAnswer() == null ? "" : ans.userAnswer().trim();
            boolean isCorrect = false;

            if ("MEANING".equals(ans.questionType())) {
                // 영어 → 뜻
                String correctMeaning = word.getMeaning() == null ? "" : word.getMeaning().trim();
                isCorrect = user.equals(correctMeaning);

            } else if ("WORD".equals(ans.questionType())) {
                // 뜻 → 영어 (대소문자 무시)
                String correctWord = word.getWord() == null ? "" : word.getWord().trim();
                isCorrect = user.equalsIgnoreCase(correctWord);
            }

            if (isCorrect) correctCount++;

            details.add(new ExamAnswerResultDto(
                    ans.wordId(),
                    ans.questionType(),
                    ans.userAnswer(),
                    isCorrect,
                    word.getWord(),
                    word.getMeaning()
            ));
        }

        boolean timeOver = request.elapsedSeconds() > 180;

        // 3) DB에 시험 결과 저장
        ExamResult saveEntity = new ExamResult();
        saveEntity.setUsername(request.username());                 // 이름
        saveEntity.setScore(correctCount);                          // 점수
        saveEntity.setTotal(request.answers().size());              // 총 문항 수
        saveEntity.setElapsedSeconds(request.elapsedSeconds());     // 걸린 시간(초)
        saveEntity.setTakenAt(LocalDateTime.now());                 // 응시 시각

        examResultRepository.save(saveEntity);

        // 4) 프론트로 응답
        return new ExamResultDto(
                request.answers().size(),
                correctCount,
                timeOver,
                details
        );
    }

    // ======================= 랭킹 조회 ==========================
    @GetMapping("/ranking")
    public List<ExamResult> getRanking() {
        // 점수 높은 순 → 시간이 짧은 순 → 최근 순
        return examResultRepository
                .findTop10ByOrderByScoreDescElapsedSecondsAscTakenAtDesc();
    }

    // ======================= DTO 모음 ==========================

    // 시험 문제 1개
    public record ExamQuestionDto(
            Long id,
            String questionType,   // "MEANING" / "WORD"
            String prompt          // 화면에 보여줄 내용 (단어 또는 뜻)
    ) {}

    // 사용자가 제출한 답안 1개
    public record ExamAnswerDto(
            Long wordId,
            String questionType,   // "MEANING" / "WORD"
            String userAnswer
    ) {}

    // 시험 제출 요청 전체
    public record ExamSubmitRequest(
            String username,               // 이름 (랭킹용)
            List<ExamAnswerDto> answers,   // 답안 리스트
            long elapsedSeconds            // 걸린 시간(초)
    ) {}

    // 각 문항별 채점 결과
    public record ExamAnswerResultDto(
            Long wordId,
            String questionType,
            String userAnswer,
            boolean correct,
            String correctWord,
            String correctMeaning
    ) {}

    // 시험 결과 응답 (프론트로 보내는 요약)
    public record ExamResultDto(
            int totalQuestions,
            int correctCount,
            boolean timeOver,
            List<ExamAnswerResultDto> details
    ) {}
}
