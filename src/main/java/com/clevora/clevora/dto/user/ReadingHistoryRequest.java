package com.clevora.clevora.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingHistoryRequest {
    private int mangaId;
    private int chapterId;
    private int page;
}
