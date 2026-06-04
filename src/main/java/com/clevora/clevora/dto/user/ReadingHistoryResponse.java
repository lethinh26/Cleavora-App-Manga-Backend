package com.clevora.clevora.dto.user;

import com.clevora.clevora.entity.ReadingHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingHistoryResponse {
    private int mangaId;
    private int chapterId;
    private int lastPage;
    private LocalDateTime lastReadAt;

    public static ReadingHistoryResponse buildingFromEntity(ReadingHistory readingHistory){
        return ReadingHistoryResponse.builder()
                .mangaId(readingHistory.getManga().getId())
                .chapterId(readingHistory.getChapterId())
                .lastPage(readingHistory.getLastPage())
                .lastReadAt(readingHistory.getLastReadAt())
                .build();
    }
}
