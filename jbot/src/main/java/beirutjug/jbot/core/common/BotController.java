package beirutjug.jbot.core.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for different Event types in Slack RTM API, Fb Messenger Bot API.
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotController {
    EventType[] events() default EventType.MESSAGE;
    
    String pattern() default "";
    
    String next() default "";
}
