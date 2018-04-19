package beirutjug.jbot.app.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Result {

    @JsonProperty("category")
    public String category;
    @JsonProperty("type")
    public String type;
    @JsonProperty("difficulty")
    public String difficulty;
    @JsonProperty("question")
    public String question;
    @JsonProperty("correct_answer")
    public String correctAnswer;
    @JsonProperty("incorrect_answers")
    public List<String> incorrectAnswers = null;

}
