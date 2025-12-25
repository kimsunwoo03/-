package com.example.gongnamul_project.words;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
    private final WordRepository repo;
//    @Override
//    public void updateMeaningFromApi() {
//        List<WordSaving> incompleteWords = repo.findAll();
//        for (WordSaving wordEntity : incompleteWords) {
//            // 이미 뜻이 채워져 있다면 스킵 (효율성)
//            if (!wordEntity.getMeaning().equals("정의 필요 (Bulk Import)")) {
//                continue;
//            }
//            try {
//    }
    //html 태그 지우는 매서드
    public String removeHtmlTags(String text) {
        if(text == null) {
            return "";
        }
        //html태그를 찾는 정규식
        String htmlTagPattern = "<[^>]*>";
        //html 태그를 공백으로 치환
        String cleanedText = text.replaceAll(htmlTagPattern, "");

        return cleanedText;
    }
    //findAll
    @Override
    public List<WordSaving> findAll() {
        return repo.findAll();
    }
    //save
    @Override
    public WordSaving save(WordSaving w) {
        return repo.save(w);
    }
    @Override
    public WordSaving findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("ID " + id + "에 해당하는 단어를 찾을 수 없습니다."));
    }
    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }
    public Optional<WordSaving> findByWord(String queryWord) {
        return repo.findByWord(queryWord);
    }

    @Override
    public int bulkSaveWords(String rawTextContent) {
        if (rawTextContent == null || rawTextContent.trim().isEmpty()) {
            return 0;
        }
        String[] lines = rawTextContent.split("\\r?\\n");
        List<WordSaving> wordsToSave = new ArrayList<>();

        for (String line : lines) {
            String cleanWord = removeHtmlTags(line.trim());
            if (cleanWord.isEmpty()) {
                continue;
            }
            WordSaving newWord = new WordSaving();
            newWord.setWord(cleanWord);
            newWord.setMeaning("정의 필요 (Bulk Import)");
            wordsToSave.add(newWord);
        }
        repo.saveAll(wordsToSave);
        return wordsToSave.size();
    }
}
