package com.amazon.asksdk.retromaster;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.amazon.asksdk.retromaster.storage.RetroMasterDao;
import com.amazon.asksdk.retromaster.storage.RetroMasterDynamoDbClient;
import com.amazon.asksdk.retromaster.storage.RetroMasterMeeting;
import com.amazon.asksdk.retromaster.storage.RetroMasterMeetingData;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * The {@link RetroMasterManager} receives various events and intents and manages the flow of the
 * meeting.
 */
public class RetroMasterManager {
    /**
     * Intent slot for whatWentWellItem.
     */
    private static final String SLOT_WHAT_WENT_WELL_ITEM = "WhatWentWellItem";

    /**
     * Intent slot for WhatWentNotWellItem.
     */
    private static final String SLOT_WHAT_WENT_NOT_WELL_ITEM = "WhatWentNotWellItem";

    /**
     * Intent slot for WhatNeedToChangeItem.
     */
    private static final String SLOT_WHAT_NEED_TO_CHANGE_ITEM = "WhatNeedToChangeItem";

    private final RetroMasterDao retroMasterDao;

    public RetroMasterManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
        RetroMasterDynamoDbClient dynamoDbClient =
                new RetroMasterDynamoDbClient(amazonDynamoDbClient);
        retroMasterDao = new RetroMasterDao(dynamoDbClient);
    }

    /**
     * Creates and returns response for Launch request.
     *
     * @param request
     *            {@link LaunchRequest} for this request
     * @param session
     *            Speechlet {@link Session} for this request
     * @return response for launch request
     */
    public SpeechletResponse getLaunchResponse(LaunchRequest request, Session session) {
        // Speak welcome message and ask user questions
        // based on whether there is a meeting now or not.
        String speechText, repromptText;
        RetroMasterMeeting meeting = retroMasterDao.getRetroMasterMeeting(session);
        if (meeting == null) {
            speechText = "Retro Master, Let's continue your retro meeting. What do you want to add to a list?";
            repromptText = "Please tell me what do you want to add to a list?";
        } else {
            speechText = "Retro Master, What can I do for you?";
            repromptText = RetroMasterTextUtil.NEXT_HELP;
        }
        return getAskSpeechletResponse(speechText, repromptText);
    }

    /**
     * Creates and returns response for the add what went well item intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            Speechlet {@link Session} for this request
     * @param skillContext
     * @return response for the add list intent.
     */
    public SpeechletResponse getAddWhatWentWellItemIntentResponse(Intent intent, Session session,
            SkillContext skillContext) {
        // add an item to the current meeting,
        // terminate or continue the conversation based on whether the intent
        // is from a one shot command or not.
        String something = intent.getSlot(SLOT_WHAT_WENT_WELL_ITEM).getValue();
        if (something == null) {
            String speechText = "OK. What do you want to add to what went well list?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        // Load the previous meeting
        RetroMasterMeeting meeting = retroMasterDao.getRetroMasterMeeting(session);
        if (meeting == null) {
            meeting = RetroMasterMeeting.newInstance(session, RetroMasterMeetingData.newInstance());
        }

        meeting.addWhatWentWell(something);
        updateMeetingData(meeting);
    }

    /**
     * Creates and returns response for the add what went not well item intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            Speechlet {@link Session} for this request
     * @param skillContext
     * @return response for the add list intent.
     */
    public SpeechletResponse getAddWhatWentNotWellItemIntentResponse(Intent intent, Session session,
            SkillContext skillContext) {
        // add an item to the current meeting,
        // terminate or continue the conversation based on whether the intent
        // is from a one shot command or not.
        String something = intent.getSlot(SLOT_WHAT_WENT_NOT_WELL_ITEM).getValue();
        if (something == null) {
            String speechText = "OK. What do you want to add to what went not well list?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        // Load the previous meeting
        RetroMasterMeeting meeting = retroMasterDao.getRetroMasterMeeting(session);
        if (meeting == null) {
            meeting = RetroMasterMeeting.newInstance(session, RetroMasterMeetingData.newInstance());
        }

        meeting.addWhatWentNotWell(something);
        updateMeetingData(meeting);
    }

    /**
     * Creates and returns response for the add what need to change item intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            Speechlet {@link Session} for this request
     * @param skillContext
     * @return response for the add list intent.
     */
    public SpeechletResponse getAddItemIntentResponse(Intent intent, Session session,
            SkillContext skillContext) {
        // add an item to the current meeting,
        // terminate or continue the conversation based on whether the intent
        // is from a one shot command or not.
        String something = intent.getSlot(SLOT_WHAT_NEED_TO_CHANGE_ITEM).getValue();
        if (something == null) {
            String speechText = "OK. What do you want to add to what need to change list?";
            return getAskSpeechletResponse(speechText, speechText);
        }
        // Load the previous meeting
        RetroMasterMeeting meeting = retroMasterDao.getRetroMasterMeeting(session);
        if (meeting == null) {
            meeting = RetroMasterMeeting.newInstance(session, RetroMasterMeetingData.newInstance());
        }

        meeting.addWhatNeedToChange(something);
        updateMeetingData(meeting);
    }

    /**
     * Creates and returns response for the help intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @param skillContext
     *            {@link SkillContext} for this request
     * @return response for the help intent
     */
    public SpeechletResponse getHelpIntentResponse(Intent intent, Session session,
            SkillContext skillContext) {
        return skillContext.needsMoreHelp() ? getAskSpeechletResponse(
                RetroMasterTextUtil.COMPLETE_HELP + " So, how can I help?",
                RetroMasterTextUtil.NEXT_HELP)
                : getTellSpeechletResponse(RetroMasterTextUtil.COMPLETE_HELP);
    }

    /**
     * Creates and returns response for the exit intent.
     *
     * @param intent
     *            {@link Intent} for this request
     * @param session
     *            {@link Session} for this request
     * @param skillContext
     *            {@link SkillContext} for this request
     * @return response for the exit intent
     */
    public SpeechletResponse getExitIntentResponse(Intent intent, Session session,
            SkillContext skillContext) {
        return skillContext.needsMoreHelp() ? getTellSpeechletResponse("Okay. Whenever you're "
                + "ready, you can start adding items to the lists in your current meeting.")
                : getTellSpeechletResponse("");
    }

    /**
     * Returns an ask Speechlet response for a speech and reprompt text.
     *
     * @param speechText
     *            Text for speech output
     * @param repromptText
     *            Text for reprompt output
     * @return ask Speechlet response for a speech and reprompt text
     */
    private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    /**
     * Returns a tell Speechlet response for a speech and reprompt text.
     *
     * @param speechText
     *            Text for speech output
     * @return a tell Speechlet response for a speech and reprompt text
     */
    private SpeechletResponse getTellSpeechletResponse(String speechText) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }
    /**
     * Helper method(s)
     *
     */
    private SpeechletResponse updateMeetingData(RetroMasterMeeting meeting){
        // Save the updated meeting
        retroMasterDao.saveRetroMasterMeeting(meeting);

        String speechText = something + " has been added to what went not well list. ";
        String repromptText = null;

        if (skillContext.needsMoreHelp()) {
            repromptText = RetroMasterTextUtil.NEXT_HELP;
        }
        if (repromptText != null) {
            return getAskSpeechletResponse(speechText, repromptText);
        } else {
            return getTellSpeechletResponse(speechText);
        }
    }
}
