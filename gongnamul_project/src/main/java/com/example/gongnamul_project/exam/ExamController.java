package com.example.gongnamul_project.exam;

import com.example.gongnamul_project.words.WordRepository;
import com.example.gongnamul_project.words.WordSaving;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final WordRepository wordRepository;

    /**
     * 시험 문제 20개 가져오기
     * - 앞 10개: 영어 → 뜻 쓰기 (questionType = "MEANING")
     * - 뒤 10개: 뜻 → 영어 쓰기 (questionType = "WORD")
     */
    @GetMapping("/questions")
    public List<ExamQuestionDto> getQuestions() {
        List<WordSaving> words = wordRepository.findAll();

        if (words.size() < 1) {
            throw new IllegalStateException("등록된 단어가 없습니다.");
        }

        // 단어 섞기
        Collections.shuffle(words);

        // 최대 20개만 사용 (단어가 20개보다 적으면 있는 만큼만 사용)
        int questionCount = Math.min(20, words.size());
        List<WordSaving> selected = words.subList(0, questionCount);

        List<ExamQuestionDto> questions = new ArrayList<>();

        for (int i = 0; i < selected.size(); i++) {
            WordSaving w = selected.get(i);

            if (i < 10) {
                // 영어 → 뜻 쓰기
                questions.add(new ExamQuestionDto(
                        w.getId(),
                        "MEANING",
                        w.getWord()  // 화면에 영어 단어 보여줌
                ));
            } else {
                // 뜻 → 영어 쓰기
                questions.add(new ExamQuestionDto(
                        w.getId(),
                        "WORD",
                        w.getMeaning() // 화면에 한국어 뜻 보여줌
                ));
            }
        }

        // 문제 순서 한번 더 섞어서 완전 랜덤 느낌
        Collections.shuffle(questions);

        return questions;
    }

    /**
     * 시험 제출
     * - 사용자가 20개 답안을 보내면 채점해서 리턴
     * - elapsedSeconds > 180이면 timeOver = true
     */
    @PostMapping("/submit")
    public ExamResultDto submitExam(@RequestBody ExamSubmitRequest request) {
        // wordId 로 단어 한 번에 조회
        List<Long> ids = request.answers().stream()
                .map(ExamAnswerDto::wordId)
                .distinct()
                .toList();

        Map<Long, WordSaving> wordMap = wordRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(WordSaving::getId, Function.identity()));

        int correctCount = 0;
        List<ExamAnswerResultDto> details = new ArrayList<>();

        for (ExamAnswerDto ans : request.answers()) {
            WordSaving word = wordMap.get(ans.wordId());
            if (word == null) {
                continue;
            }

            String user = ans.userAnswer() == null ? "" : ans.userAnswer().trim();
            boolean isCorrect = false;

            if ("MEANING".equals(ans.questionType())) {
                // 뜻 쓰기: meaning 정확히 일치 (간단 버전)
                String correctMeaning = word.getMeaning() == null ? "" : word.getMeaning().trim();
                isCorrect = user.equals(correctMeaning);
            } else if ("WORD".equals(ans.questionType())) {
                // 영단어 쓰기: 대소문자 무시
                String correctWord = word.getWord() == null ? "" : word.getWord().trim();
                isCorrect = user.equalsIgnoreCase(correctWord);
            }

            if (isCorrect) {
                correctCount++;
            }

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

        return new ExamResultDto(
                request.answers().size(),
                correctCount,
                timeOver,
                details
        );
    }

    // ====== DTO 정의들 (이 파일 안에 같이 넣어서 관리) ======

    // 문제 1개
    public record ExamQuestionDto(
            Long id,           // WordSaving id
            String questionType, // "MEANING" or "WORD"
            String prompt        // 화면에 보여줄 내용 (단어 or 뜻)
    ) {}

    // 사용자가 보낸 답안 1개
    public record ExamAnswerDto(
            Long wordId,
            String questionType, // "MEANING" / "WORD"
            String userAnswer
    ) {}

    // 제출 요청 전체
    public record ExamSubmitRequest(
            List<ExamAnswerDto> answers,
            long elapsedSeconds // 걸린 시간(초) - 프론트에서 보내줌
    ) {}

    // 각 문항 채점 결과
    public record ExamAnswerResultDto(
            Long wordId,
            String questionType,
            String userAnswer,
            boolean correct,
            String correctWord,
            String correctMeaning
    ) {}

    // 전체 시험 결과
    public record ExamResultDto(
            int totalQuestions,
            int correctCount,
            boolean timeOver,
            List<ExamAnswerResultDto> details
    ) {}
}
