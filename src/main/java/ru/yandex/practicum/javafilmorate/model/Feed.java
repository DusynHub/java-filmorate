package ru.yandex.practicum.javafilmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Data
@Builder
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
