package beirutjug.jbot.app.slack;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beirutjug.jbot.core.slack.models.interactive.ResponseMessage;


@RestController
@Profile("slack")
public class SlackInteractiveMessageController {

    private static final Logger logger = LoggerFactory.getLogger(SlackInteractiveMessageController.class);
    
    @Value("${slackVerificationToken}")
    private String slackVerificationToken;

    private static String[] correct_reactions = { "Correct! :+1:", "Right! :smile:", "Correct! :sunglasses:", ":+1:" };

    @Autowired
    private SlackBot slackBot;

    private Random random = new Random();

    @Autowired
    private SlackWebApiInvoker slackWebApiInvoker;

    @RequestMapping(value = "/interactive", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> onReceiveInteractiveRequest(@RequestBody MultiValueMap<String, Object> paramMap) {
        String payload = (String) ((List) paramMap.get("payload")).get(0);
        try {
            logger.info(new ObjectMapper().writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            logger.warn("Error parsing RichMessage: ", e);
        }
        logger.info("Verifying against " + slackVerificationToken);

        ResponseMessage message;
        try {
            message = new ObjectMapper().readValue((String)payload, ResponseMessage.class);
            String token = message.getToken();
            if(!slackVerificationToken.equals(token)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            String answer = message.getActions().get(0).getValue();
            String correctAnswer = slackBot.currentCorrectAnswerMap.get(message.getUser().getId());
            logger.info("Correct answer :" + correctAnswer);
            if(correctAnswer.equalsIgnoreCase(answer)) {
                slackWebApiInvoker.postMessage(message.getChannel().getId(), correct_reactions[random.nextInt(correct_reactions.length)] +
                        "\nAnother question?", null);
            } else {
                slackWebApiInvoker.postMessage(message.getChannel().getId(), "Wrong answer :expressionless:\nAnother question?", null);
            }
            return ResponseEntity.ok().body(null);
        } catch (IOException e) {
            logger.warn("Error parsing response message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
