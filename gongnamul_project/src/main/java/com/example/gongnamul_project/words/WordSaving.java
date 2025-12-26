package com.example.gongnamul_project.words;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class WordSaving {
    @Id @GeneratedValue private Long id;
    @Column(nullable = false) private String word;
    @Column(nullable = false) private String meaning;

}
