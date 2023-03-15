package ru.yandex.practicum.javafilmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Feed {
    private Long eventId;
    @NotBlank
    private Long userId;
    @NotBlank
    private String eventType;
    @NotBlank
    @JsonProperty("operation")
    private String operationType;
    @NotBlank
    private Long entityId;
    @NotBlank
    @JsonProperty("timestamp")
    private Long feedDate;
}
