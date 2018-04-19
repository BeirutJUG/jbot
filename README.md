# JBot [![Build Status](https://travis-ci.org/ramswaroop/jbot.svg?branch=master)](https://travis-ci.org/ramswaroop/jbot) [![Codacy](https://api.codacy.com/project/badge/Grade/569b52fd935042538309d8f45e9d8b70)](https://www.codacy.com/app/ramswaroop/jbot) [![Javadocs](http://www.javadoc.io/badge/me.ramswaroop.jbot/jbot.svg?color=orange)](http://www.javadoc.io/doc/me.ramswaroop.jbot/jbot) [![Facebook](https://img.shields.io/badge/social-fb-blue.svg)](https://www.facebook.com/jbotframework/) [![Gitter](https://badges.gitter.im/ramswaroop/jbot.svg)](https://gitter.im/ramswaroop/jbot?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Backers on Open Collective](https://opencollective.com/jbot/backers/badge.svg)](#backers) [![Sponsors on Open Collective](https://opencollective.com/jbot/sponsors/badge.svg)](#sponsors) [![MIT license](https://img.shields.io/badge/license-GPL_3.0-yellow.svg)](https://raw.githubusercontent.com/ramswaroop/jbot/master/LICENSE)

__This project is based on https://github.com/ramswaroop/jbot and has been modified to support the implementation of the trivia bot on Slack. Controller annotation is renamed to BotController to avoid confusion with Spring annotation. Other JSON model POJOs were added to support responses to interactive messages (i.e. button selection).__

See https://api.slack.com/ for lots of documentation about Slack app/bot integration.

* Create a Slack app at https://api.slack.com/apps and fill the basic information
* Install the app to your workspace
* Add a bot user (using the Bot Users link in the app configuration page)

To configure the Slack trivia app correct, modify the [application.properties](/jbot-slack-trivia-app/src/main/resources/application.properties) file:

* Set the bot user OAuth access token in the property `slackBotToken`. You can retrive it from the link OAuth & Permissions. It starts with `xoxb-`.
* Create an incoming webhook (from the link Incoming Webhooks) and add its URL in the `slackIncomingWebhookUrl` property.
* Set the property `slackVerificationToken`. You can retrieve the token from Basic Information -> App Credentials
* Finally, go to Interactive Components and add a Request URL that points to the URL on which `SlackInteractiveMessageController` is listening. You can either use [ngrok](https://ngrok.com/) to publish a URL for your locally running server, or deploy your server on Heroku.
* If you choose Heroku, follow the steps in [this page](https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku).


Make bots in Java.

__JBot__ is a java framework _(inspired by [Howdyai's Botkit](https://github.com/howdyai/botkit))_ to
make Slack and Facebook bots in minutes. It provides all the boilerplate code needed so that you
can make your bot live right away.

## Why use JBot?

* Provides you with __all the boilerplate code__ which handles underlying websocket connections and other complexities.
* Supports __extra events__ in addition to all the events supported by Slack/Facebook which makes your work a lot more easier.
* __Receiving & sending messages__ is as easy as defining a `@Controller` and calling `reply()`.
* __Conversation feature__ of JBot makes talking to your bot a breeze. This feature makes JBot different than rest of the Java frameworks out there.
* __Well tested__ with good coverage unit tests.
* And many other features which can't just be mentioned here.

**Not satisfied?** Read on...

* JBot got more than __400 stars__ in just 2 days after release.
* It is in the [Hacker News](https://news.ycombinator.com/item?id=12239667) 50 club.
* Chosen by [DZone daily picks](http://mailer.dzone.com/display.php?M=15184241&C=dcebb6887365120539df1fbf19a071ed&S=9043&L=658&N=4604).
* Last but not the least, it's listed on [Slack.com](https://api.slack.com/community)

**Still worried?** Open an [issue on Github](https://github.com/ramswaroop/jbot/issues) and we can discuss.

## JBot for Slack

**Running your SlackBot is just 4 easy steps:**
  
1. Clone this project `$ git clone git@github.com:ramswaroop/jbot.git`.  
2. [Create a slack bot](https://my.slack.com/services/new/bot) and get your slack token.  
3. Paste the token in [application.properties](/jbot-slack-trivia-app/src/main/resources/application.properties) file.  
4. Run the example application by running `JBotApplication` in your IDE or via commandline: 
    ```bash
    $ cd jbot
    $ mvn clean install
    $ cd jbot-example
    $ mvn spring-boot:run
    ```

You can now start talking with your bot ;)

Read the detailed [Slack documentation](/docs/slack/README-Slack-JBot-4.0.0.md) to learn more.

## JBot for Facebook

**Similar to Slack, Facebook is simple too but has few extra steps:**

1. Clone this project `$ git clone git@github.com:ramswaroop/jbot.git`.
2. Create a [facebook app](https://developers.facebook.com/docs/apps/register#create-app) and a 
[page](https://www.facebook.com/pages/create).
3. Generate a Page Access Token for the page (inside app's messenger settings).
4. Paste the token created above in [application.properties](/jbot-slack-trivia-app/src/main/resources/application.properties) file.
5. Run the example application by running `JBotApplication` in your IDE or via commandline: 
    ```bash
    $ cd jbot
    $ mvn clean install
    $ cd jbot-example
    $ mvn spring-boot:run
    ```
6. Setup webhook to receive messages and other events. You need to have a public address to setup webhook. You may use
[localtunnel.me](https://localtunnel.me) to generate a public address if you're running locally on your machine.
7. Specify the address created above in "Callback Url" field under "Webooks" setting and give the verify token 
as `fb_token_for_jbot` and click "Verify and Save".

You can now start messaging your bot by going to the facebook page and clicking on the "Send message" button. 

_If you're too lazy to start now and just want to play around, you can try `jbot-example` by visiting 
[JBot facebook page](https://www.facebook.com/jbotframework/) and clicking on the "Send Message" button._

Read the detailed [Facebook documentation](/docs/facebook/README-Facebook-JBot-4.0.0.md) to learn more.

