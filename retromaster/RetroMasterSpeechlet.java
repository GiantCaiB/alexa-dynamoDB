package com.amazon.asksdk.retromaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


public class RetroMasterSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(RetroMasterSpeechlet.class);

    private AmazonDynamoDBClient amazonDynamoDBClient;

    private RetroMasterManager retroMasterManager;

    private SkillContext skillContext;

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());

        initializeComponents();

        // if user said a one shot command that triggered an intent event,
        // it will start a new session, and then we should avoid speaking too many words.
        skillContext.setNeedsMoreHelp(false);
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());

        skillContext.setNeedsMoreHelp(true);
        return retroMasterManager.getLaunchResponse(requestEnvelope.getRequest(), requestEnvelope.getSession());
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        initializeComponents();

        Intent intent = request.getIntent();
        if ("AddWhatWentWellItemIntent".equals(intent.getName())) {
            return retroMasterManager.getAddWhatWentWellItemIntentResponse(intent,session, skillContext);

        } else if ("AddWhatWentNotWellItemIntent".equals(intent.getName())) {
            return retroMasterManager.getAddWhatWentNotWellItemIntentResponse(intent, session, skillContext);

        } else if ("AddWhatNeedToChangeItemIntent".equals(intent.getName())) {
            return retroMasterManager.getAddWhatNeedToChangeItemIntentResponse(intent, session, skillContext);

        }else if ("AMAZON.HelpIntent".equals(intent.getName())) {
            return retroMasterManager.getHelpIntentResponse(intent, session, skillContext);

        } else if ("AMAZON.CancelIntent".equals(intent.getName())) {
            return retroMasterManager.getExitIntentResponse(intent, session, skillContext);

        } else if ("AMAZON.StopIntent".equals(intent.getName())) {
            return retroMasterManager.getExitIntentResponse(intent, session, skillContext);

        } else {
            throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Initializes the instance components if needed.
     */
    private void initializeComponents() {
        if (amazonDynamoDBClient == null) {
            amazonDynamoDBClient = new AmazonDynamoDBClient();
            retroMasterManager = new RetroMasterManager(amazonDynamoDBClient);
            skillContext = new SkillContext();
        }
    }
}
