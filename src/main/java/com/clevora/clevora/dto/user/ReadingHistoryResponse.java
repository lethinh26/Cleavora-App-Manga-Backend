package com.clevora.clevora.dto.user;

import java.time.LocalDateTime;

import com.clevora.clevora.entity.ReadingHistory;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingHistoryResponse {
    private int mangaId;
    private Integer chapterId;
    private Double chapterNumber;   // số chương thực tế (1.0, 1.5, ...) để hiển thị
    private int lastPage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastReadAt;

    public static ReadingHistoryResponse buildingFromEntity(ReadingHistory readingHistory) {
        return ReadingHistoryResponse.builder()
                .mangaId(readingHistory.getManga().getId())
                .chapterId(readingHistory.getChapterId())
                .lastPage(readingHistory.getLastPage())
                .lastReadAt(readingHistory.getLastReadAt())
                .build();
    }

    public static ReadingHistoryResponse buildingFromEntity(ReadingHistory readingHistory, Double chapterNumber) {
        return ReadingHistoryResponse.builder()
                .mangaId(readingHistory.getManga().getId())
                .chapterId(readingHistory.getChapterId())
                .chapterNumber(chapterNumber)
                .lastPage(readingHistory.getLastPage())
                .lastReadAt(readingHistory.getLastReadAt())
                .build();
    }
}
