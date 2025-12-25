package com.example.gongnamul_project.words;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
@CrossOrigin(origins= "http://localhost:3000") //프론트 포트에 따라 수정 그냥 3000번 포트로 해놓음
public class WordController {
    private final WordRepository repository; // 기존에 있는 레포지토리 이름에 맞게 수정하세요

    @GetMapping("/words")
    public List<WordSaving> getAllWords() {
        return repository.findAll();
    }

    private final WordService wordService; //DB랑 연결

    //단어 추가
    @PostMapping("/crawl")
    public WordSaving create(@RequestBody WordSaving w) {
        return wordService.save(w);
    }
    //단어 목록 보기
    @GetMapping
    public List<WordSaving> list() {
        return wordService.findAll();
    }

    //단어 수정
    @PutMapping("/{id}")
    public WordSaving update(@PathVariable Long id, @RequestBody WordSaving w) {
        WordSaving x = wordService.findById(id);
        x.setWord(w.getWord());
        x.setMeaning(w.getMeaning());
        return wordService.save(x);
    }
    //단어 삭제
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        wordService.deleteById(id);
    }
    @PostMapping("/bulk-import")
    public ResponseEntity<String> bulkImport(@RequestBody String rawText) {
        try {
            int count = wordService.bulkSaveWords(rawText);
            return ResponseEntity.ok("데이터베이스에 총 " + count + "개의 단어가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("데이터 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
