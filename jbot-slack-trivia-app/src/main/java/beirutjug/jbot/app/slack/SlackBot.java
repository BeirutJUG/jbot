package beirutjug.jbot.app.slack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import beirutjug.jbot.app.model.Result;
import beirutjug.jbot.app.model.Trivia;
import beirutjug.jbot.core.common.BotController;
import beirutjug.jbot.core.common.EventType;
import beirutjug.jbot.core.common.JBot;
import beirutjug.jbot.core.slack.Bot;
import beirutjug.jbot.core.slack.models.Event;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@JBot
@Profile("slack")
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    private RestTemplate restTemplate = new RestTemplate();

    protected Map<String, String> currentCorrectAnswerMap = new HashMap<>();
    protected Map<String, String> currentQuestionTypeMap = new HashMap<>();

    private static Map<String, String> categories = new HashMap<>();

    static {
        categories.put("sport", "21");
        categories.put("history", "23");
        categories.put("computer", "18");
        categories.put("game", "15");
        categories.put("movie", "11");
        categories.put("film", "11");
    }

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Autowired
    private SlackWebApiInvoker slackWebApiInvoker;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    /**
     * Invoked when the bot receives a direct mention (@botname: message)
     * or a direct message. NOTE: These two event types are added by jbot
     * to make your task easier, Slack doesn't have any direct way to
     * determine these type of events.
     *
     * @param session
     * @param event
     */
    @BotController(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
        reply(session, event, "Hi, I am " + slackService.getCurrentUser().getName());
    }

    /**
     * Invoked when bot receives an event of type message with text satisfying
     * the pattern {@code ([a-z ]{2})(\d+)([a-z ]{2})}. For example,
     * messages like "ab12xy" or "ab2bc" etc will invoke this method.
     *
     * @param session
     * @param event
     */
    @BotController(events = EventType.MESSAGE, pattern = "^([a-z ]{2})(\\d+)([a-z ]{2})$")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        reply(session, event, "First group: " + matcher.group(0) + "\n" +
                "Second group: " + matcher.group(1) + "\n" +
                "Third group: " + matcher.group(2) + "\n" +
                "Fourth group: " + matcher.group(3));
    }

    /**
     * Invoked when an item is pinned in the channel.
     *
     * @param session
     * @param event
     */
    @BotController(events = EventType.PIN_ADDED)
    public void onPinAdded(WebSocketSession session, Event event) {
        reply(session, event, "Thanks for the pin! You can find all pinned items under channel details.");
    }

    /**
     * Invoked when bot receives an event of type file shared.
     * NOTE: You can't reply to this event as slack doesn't send
     * a channel id for this event type. You can learn more about
     * <a href="https://api.slack.com/events/file_shared">file_shared</a>
     * event from Slack's Api documentation.
     *
     * @param session
     * @param event
     */
    @BotController(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
        logger.info("File shared: {}", event);
    }

    /**
     * Conversation feature of JBot. This method is the starting point of the conversation (as it
     * calls {@link Bot#startConversation(Event, String)} within it. You can chain methods which will be invoked
     * one after the other leading to a conversation. You can chain methods with {@link BotController#next()} by
     * specifying the method name to chain with.
     *
     * @param session
     * @param event
     */
    @BotController(pattern = "(?i)(ask .+)", next = "checkAnswer")
    public void askTrivia(WebSocketSession session, Event event) {
        startConversation(event, "checkAnswer");   // start conversation
        String category = "";
        WhitespaceTokenizer whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
        List<String> tokens = Arrays.asList(whitespaceTokenizer.tokenize(event.getText().toLowerCase()));
        for(Iterator<String> it = categories.keySet().iterator(); it.hasNext();) {
            String catKey = it.next();
            if(tokens.stream().anyMatch(token -> token.contains(catKey))) {
                category = "&category=" + categories.get(catKey);
                break;
            }
        }
        ResponseEntity<Trivia> response = restTemplate.getForEntity("https://opentdb.com/api.php?amount=1" + category, Trivia.class);
        Trivia responseBody = response.getBody();
        if (responseBody != null) {
            Result trivia = responseBody.getResults().get(0);

            String userId = event.getUserId();
            String correctAnswer = trivia.getCorrectAnswer();
            currentCorrectAnswerMap.put(userId, correctAnswer);
            
            String type = trivia.getType();
            currentQuestionTypeMap.put(userId, type);
            if(type.equals("multiple")) {
                List<String> answers = new ArrayList<>();
                answers.add(correctAnswer);
                answers.addAll(trivia.getIncorrectAnswers());

                slackWebApiInvoker.postMessage(event.getChannelId(), StringEscapeUtils.unescapeHtml(trivia.getQuestion()), answers);
            } else if(type.equals("boolean")) {
                List<String> answers = new ArrayList<>();
                answers.add("True");
                answers.add("False");

                slackWebApiInvoker.postMessage(event.getChannelId(), trivia.getQuestion(), answers);
            } else {
                reply(session, event, trivia.getQuestion());
            }
        } else {
            logger.debug("Trivia response invalid. Response: {}", response);
            reply(session, event, "Trivia response invalid");
            stopConversation(event);
        }
    }

    @BotController(next = "askAgain")
    public void checkAnswer(WebSocketSession session, Event event) {
        String currentQuestionType = currentQuestionTypeMap.get(event.getUserId());
        if(!currentQuestionType.equals("multiple") && !currentQuestionType.equals("boolean")) {
            String currentCorrectAnswer = currentCorrectAnswerMap.get(event.getUserId());
            if(event.getText().equalsIgnoreCase(currentCorrectAnswer)) {
                reply(session, event, "Correct! :)\nAnother question?");
            } else {
                reply(session, event, "Wrong answer :(\nAnother question?");
            }
            nextConversation(event);    // jump to next question in conversation
        } else {
            stopConversation(event);
            askAgain(session, event);
        }
    }

    @BotController
    public void askAgain(WebSocketSession session, Event event) {
        stopConversation(event);
        if(event.getText().toLowerCase().startsWith("yes")) {
            askTrivia(session, event);
        }
    }

}