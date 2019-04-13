package co.blasthack.mood.text.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MoodTextMessage {

    @JsonInclude
    private String id;

    @JsonInclude
    private String message;
}
