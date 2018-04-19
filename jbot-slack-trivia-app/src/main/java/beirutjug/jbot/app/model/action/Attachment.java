
package beirutjug.jbot.app.model.action;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment {

    @JsonProperty("callback_id")
    public String callbackId;
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("actions")
    public List<Action_> actions = null;
    @JsonProperty("fallback")
    public String fallback;

}
