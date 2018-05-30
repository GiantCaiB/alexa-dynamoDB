package com.danny.alexa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.danny.util.TextUtils;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;

public class Speechlet implements SpeechletV2
{
	private static final Logger log = LoggerFactory.getLogger(Speechlet.class);
	private SpeechletManager speechletManager = new SpeechletManager();

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope)
	{
	    log.info(TextUtils.LOG_SESSION_STARTED_INFO(requestEnvelope));
	    try
	    {
	    		requestEnvelope.getSession().setAttribute(TextUtils.KEY_TEAM_ID, null);
		}
	    catch (Exception exp)
	    {
	    		log.error(TextUtils.EXCEPTION_SESSION_STARTED(exp));
		}
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope)
	{
		log.info(TextUtils.LOG_LAUNCH_INFO(requestEnvelope));
		return speechletManager.getWelcomeResponse(requestEnvelope.getSession());
	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope)
	{
		// Getting intent and session from the request envelope
	    IntentRequest request = requestEnvelope.getRequest();
	    log.info(TextUtils.LOG_INTEND_INFO(requestEnvelope));
	    Intent intent = request.getIntent();
	    Session session = requestEnvelope.getSession();
	    String intentName = (intent != null) ? intent.getName() : null;
	    if (intentName.equals(TextUtils.INTENT_DEFAULT_FALLBACK_INTENT))
	    {
    		return speechletManager.getHelpResponse(TextUtils.BOOL_UNRECOGNISED_INPUT);
	    }
	    else if (intentName.equals(TextUtils.INTENT_SELECT_TEAM))
	    {
	    	return speechletManager.getSelectTeamResponse(intent, session);
	    }
	    else if (intentName.equals(TextUtils.INTENT_START_NEW_MEETING))
	    {
	    	return speechletManager.getStartNewMeetingResponse(intent, session);
	    }
	    else if (intentName.equals(TextUtils.INTENT_CONTINUE_EXIST_MEETING))
	    {
	    	return speechletManager.getContinueExistMeetingResonse(intent, session);
	    }
	    else if (intentName.equals(TextUtils.INTENT_QUERY_TEAM_ID))
	    {
	    	return speechletManager.getQueryTeamIdResonse(session);
	    }
	    else if (intentName.equals(TextUtils.INTENT_QUERY_MEETING_ID))
	    {
	    	return speechletManager.getQueryMeetingIdResonse(session);
	    }
	    else if (intentName.equals(TextUtils.INTENT_ADD_GOOD_ITEM))
	    {
	    	return speechletManager.getAddItemResponse(intent,session,TextUtils.CAT_WHAT_WENT_WELL);
		}
	    else if (intentName.equals(TextUtils.INTENT_ADD_BAD_ITEM))
		{
	    	return speechletManager.getAddItemResponse(intent,session,TextUtils.CAT_WHAT_WENT_WRONG);
		}
	    else if (intentName.equals(TextUtils.INTENT_DELETE_LAST_ADDED_ITEM))
		{
	    	return speechletManager.getDeleteLastAddedItemResponse(session);
		}
	    else if (intentName.equals(TextUtils.INTENT_EXTEND_TIMER))
		{
	    	return speechletManager.getExtendTimerResponse(intent,session);
		}
	    else if (intentName.equals(TextUtils.INTENT_FINISH_RETRO))
		{
	    	return speechletManager.getFinishRetroResponse(session);
		}
	    else if (intentName.equals(TextUtils.INTENT_AMAZON_CANCEL_INTENT))
		{
	        return speechletManager.getCancelResponse();
	    }
	    else if (intentName.equals(TextUtils.INTENT_AMAZON_HELP_INTENT))
	    {
	        return speechletManager.getHelpResponse(!TextUtils.BOOL_UNRECOGNISED_INPUT);
	    }
	    else if (intentName.equals(TextUtils.INTENT_AMAZON_STOP_INTENT))
	    {
	        return speechletManager.getStopResponse();
	    }
	    else
	    {
    	    throw new IllegalArgumentException(TextUtils.EXCEPTION_INTENT(intent.getName()));
	    }
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope)
	{
	    log.info(TextUtils.LOG_SESSION_ENDED_INFO(requestEnvelope));
	}
}
