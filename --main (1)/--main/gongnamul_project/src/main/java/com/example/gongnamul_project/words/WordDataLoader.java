package com.example.gongnamul_project.words;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class WordDataLoader {

    private final WordService wordService;

    @PostConstruct
    public void loadWordsFromFile() {
        try {
            // 이미 단어가 있으면 다시 안 넣음 (중복 방지)
            if (!wordService.findAll().isEmpty()) {
                System.out.println("✅ word_saving 테이블에 이미 데이터가 있어서 words.txt 로딩을 건너뜁니다.");
                return;
            }

            // resources/words.txt 읽기 (형이 만든 파일 이름에 맞춤!)
            ClassPathResource resource = new ClassPathResource("words.txt");

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            String rawTextContent = sb.toString();
            int count = wordService.bulkSaveWords(rawTextContent);

            System.out.println("✅ words.txt에서 단어 " + count + "개를 DB에 저장했습니다.");

        } catch (Exception e) {
            System.out.println("❌ words.txt 로딩 중 오류 발생");
            e.printStackTrace();
        }
    }
}