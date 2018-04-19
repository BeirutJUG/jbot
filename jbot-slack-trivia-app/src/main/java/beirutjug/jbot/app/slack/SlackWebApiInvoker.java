package beirutjug.jbot.app.slack;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beirutjug.jbot.core.slack.models.Action;
import beirutjug.jbot.core.slack.models.Attachment;
import beirutjug.jbot.core.slack.models.RichMessage;

@Component
@Profile("slack")
public class SlackWebApiInvoker {

    private static final Logger logger = LoggerFactory.getLogger(SlackWebApiInvoker.class);

    @Value("${slackBotToken}")
    private String slackBotToken;

    /**
     * Sends a POST request to the chat.postMessage API containing an interactive message with buttons.
     * @param channelId
     * @param messageText
     * @param buttons
     */
    public void postMessage(String channelId, String messageText, List<String> buttons) {
        RestTemplate restTemplate = new RestTemplate();
        
        RichMessage richMessage = new RichMessage(messageText);
        richMessage.setToken(slackBotToken);
        richMessage.setChannel(channelId);
        richMessage.setAsUser(true);
        
        if(buttons != null && !buttons.isEmpty()) {
            // set attachments
            Action[] actions = new Action[buttons.size()];
            for(int i = 0; i < buttons.size(); i++) {
                actions[i] = new Action();
                actions[i].setText(buttons.get(i));
                actions[i].setType("button");
                actions[i].setName(buttons.get(i));
                actions[i].setValue(buttons.get(i));
            }
            Attachment[] attachments = new Attachment[1];
            attachments[0] = new Attachment();
            attachments[0].setActions(actions);
            attachments[0].setCallbackId("abc_123");
            richMessage.setAttachments(attachments);
        }

        // For debugging purpose only
        try {
            logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
        } catch (JsonProcessingException e) {
            logger.debug("Error parsing RichMessage: ", e);
        }

        // Always remember to send the encoded message to Slack
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + slackBotToken);

            HttpEntity<RichMessage> entity = new HttpEntity<RichMessage>(richMessage.encodedMessage(),headers);
//            restTemplate.put(uRL, entity);
            restTemplate.postForObject("https://slack.com/api/chat.postMessage", entity, String.class);
        } catch (RestClientException e) {
            logger.error("Error posting message: ", e);
        }
    }

}
