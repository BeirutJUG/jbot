
package beirutjug.jbot.app.model.action;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OriginalMessage {

    @JsonProperty("type")
    public String type;
    @JsonProperty("user")
    public String user;
    @JsonProperty("text")
    public String text;
    @JsonProperty("bot_id")
    public String botId;
    @JsonProperty("attachments")
    public List<Attachment> attachments = null;
    @JsonProperty("ts")
    public String ts;

}
