
package beirutjug.jbot.core.slack.models.interactive;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import beirutjug.jbot.core.slack.models.Action;
import beirutjug.jbot.core.slack.models.Channel;
import beirutjug.jbot.core.slack.models.Team;
import beirutjug.jbot.core.slack.models.User;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ResponseMessage {

    @JsonProperty("type")
    public String type;
    @JsonProperty("actions")
    public List<Action> actions = null;
    @JsonProperty("callback_id")
    public String callbackId;
    @JsonProperty("team")
    public Team team;
    @JsonProperty("channel")
    public Channel channel;
    @JsonProperty("user")
    public User user;
    @JsonProperty("action_ts")
    public String actionTs;
    @JsonProperty("message_ts")
    public String messageTs;
    @JsonProperty("attachment_id")
    public String attachmentId;
    @JsonProperty("token")
    public String token;
    @JsonProperty("is_app_unfurl")
    public Boolean isAppUnfurl;
    @JsonProperty("original_message")
    public OriginalMessage originalMessage;
    @JsonProperty("response_url")
    public String responseUrl;
    @JsonProperty("trigger_id")
    public String triggerId;

}
