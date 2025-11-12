package com.example.gongnamul_project.words;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor

public class WordController {
    private final WordRepository repo; //DB랑 연결
    //단어 추가
    @PostMapping
    public WordSaving create(@RequestBody WordSaving w) {
        return repo.save(w);
    }
    //단어 목록 보기
    @GetMapping
    public List<WordSaving> list() {
        return repo.findAll();
    }

    //단어 수정
    @PutMapping("/{id}")
    public WordSaving update(@PathVariable Long id, @RequestBody WordSaving w) {
        WordSaving x = repo.findById(id).orElseThrow();
        x.setWord(w.getWord());
        x.setMeaning(w.getMeaning());
        return repo.save(x);
    }
    //단어 삭제
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
