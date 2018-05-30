package com.danny.util;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;

public final class TextUtils
{
	// Invocation name
	private static final String INVOCATION_NAME = "Retro Master";
	// Intents' names
	public static final String INTENT_DEFAULT_FALLBACK_INTENT = "DefaultFallbackIntent";
	public static final String INTENT_SELECT_TEAM = "SelectTeam";
	public static final String INTENT_START_NEW_MEETING = "StartNewMeeting";
	public static final String INTENT_CONTINUE_EXIST_MEETING = "ContinueExistMeeting";
	public static final String INTENT_QUERY_TEAM_ID = "QueryTeamId";
	public static final String INTENT_QUERY_MEETING_ID = "QueryMeetingId";
	public static final String INTENT_ADD_GOOD_ITEM = "AddGoodItem";
	public static final String INTENT_ADD_BAD_ITEM = "AddBadItem";
	public static final String INTENT_DELETE_LAST_ADDED_ITEM = "DeleteLastAddedItem";
	public static final String INTENT_EXTEND_TIMER = "ExtendTimer";
	public static final String INTENT_FINISH_RETRO = "FinishRetro";
	public static final String INTENT_AMAZON_CANCEL_INTENT = "AMAZON.CancelIntent";
	public static final String INTENT_AMAZON_HELP_INTENT = "AMAZON.HelpIntent";
	public static final String INTENT_AMAZON_STOP_INTENT = "AMAZON.StopIntent";
	// Alexa session Keys
	public static final String KEY_TEAM_ID = "teamId";
	public static final String KEY_MEETING_ID = "meetingId";
	public static final String KEY_LAST_ADDED_RETRO_ITEM_ID = "lastAddedRetroItemId";
	public static final String KEY_LAST_ADDED_RETRO_ITEM_CAT = "lastAddedRetroItemCat";
	public static final String KEY_LAST_ADDED_RETRO_ITEM = "lastAddedRetroItem";
	// Alexa slot names
	public static final String SLOT_TEAM_ID = "TeamId";
    public static final String SLOT_MEETING_ID = "MeetingId";
    public static final String SLOT_MEETING_NAME = "MeetingName";
    public static final String SLOT_RETRO_ITEM = "RetroItem";
    public static final String SLOT_DURATION = "ExtendDuration";
    // DynamoDB categories
    public static final String CAT_WHAT_WENT_WELL = "WHAT WENT WELL";
    public static final String CAT_WHAT_WENT_WRONG = "WHAT WENT WRONG";
    // Meeting Stages
    public static final Integer STAGE_ADDING = 1;
    public static final Integer STAGE_GROUPING = 2;
    public static final Integer STAGE_VOTING = 3;
    public static final Integer STAGE_DISCUSSION = 4;
    // Alexa card titles
    public static final String CARD_WELCOME = "Welcome";
    public static final String CARD_TEAM_SELECTED = "Team selected";
    public static final String CARD_MEETING_NEW = "Start New Meeting";
    public static final String CARD_MEETING_CONTINUE = "Continue A Meeting";
    public static final String CARD_TEAM_QUERY = "Current Team Id";
    public static final String CARD_MEETING_QUERY = "Current Meeting Id";
    public static final String CARD_ITEM_ADDED = "Retro Item Added";
    public static final String CARD_ITEM_DELETED = "Delete Retro Item";
    public static final String CARD_TIMER_EXTENDED = "Timer Extension";
    public static final String CARD_RETRO_FINISHED = "Retro Finished";
    public static final String CARD_CANCEL = "Action Canceled";
    public static final String CARD_HELP = "Help Info";
    public static final String CARD_ERROR = "Opps";
    public static final String CARD_STOP = "Skill Stopped";
  	public static final boolean BOOL_UNRECOGNISED_INPUT = true;
  	public static final String ALEXA_HELP = "First  please tell "+ INVOCATION_NAME +" your team id. After that you can to \"start a new meeting\", or, \"continue meeting\" with an id. Then you can add retro items now by saying: \"blabla is good\", or \"blabla went wrong\".";
    public static final String ALEXA_DEFAULT_FALLBACK_INTENT = "Sorry, I didn't get that. You can say \"Help\" for further instructions.";
    public static final String ALEXA_ERROR_INFO = "Opps, something is wrong. Would you like to try something else?";
    public static final String ALEXA_ERROR_NO_MEETING = " There is no meeting record in the database, please ask " + INVOCATION_NAME + " to start a new meeting first.";
    public static final String ALEXA_ERROR_NO_LAST_ADDED_ITEM = "Sorry, your last added item had been deleted.";
    public static final String ALEXA_ERROR_NO_ADDED_ITEM = "Sorry, nothing has been added for now.";
  	// Alexa intent structure warning
  	public static final String STRUCT_TEAM_ID = "Please tell me your team id first.";
  	public static final String STRUCT_MEETING_ID = "Please select or create a meeting first.";
  	public static final String STRUCT_NO_TEAM_CHANGING = "Sorry, to change your team, please start the skill again. ";
  	public static final String STRUCT_STAGE_REQUIRED(Integer stageNumber)
  	{
  		return "Sorry, you can only do this when the meeting stage is " + STAGE(stageNumber) + ". ";
  	}
  	// Controller exceptions
  	public static final String CTRL_EXP_MEETING_ID = "Meeting does not exist in database";
  	public static final String CTRL_EXP_TEAM_INVALID = "TEAM doesn't exist in DB.";

    // Meeting Stages to String
    public static String STAGE(Integer stageNumber)
    {
		String stage = null;
		switch(stageNumber)
		{
			case 1: stage = "\"Adding\"";
					break;
			case 2: stage = "\"Grouping\"";
					break;
			case 3: stage = "\"Voting\"";
				break;
			case 4: stage = "\"Discussion\"";
            break;
			default: stage = "\"Invalid stage\"";
            break;
		}
		return stage;
    }

    // Various speech from ALexa
    private static final String[] WELCOME = new String[]
    {
		"Welcome to " + INVOCATION_NAME + ". ",
		"G'day, this is " + INVOCATION_NAME + ". ",
		"Hi there, this is " + INVOCATION_NAME + ". ",
		"Greetings, this is " + INVOCATION_NAME + ". ",
		"Hello, this is " + INVOCATION_NAME + ". ",
    };

    private static final String[] GOODBYE = new String[]
    {
		"Okay, see ya.",
		"No problem, bye!",
		"Alright, good bye!",
		"See you soon.",
		"No worries, see ya.",
    };
    // Log info
  	public static final String LOG_SESSION_STARTED_INFO(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope)
  	{
  		return "onSessionStarted requestId= "+ requestEnvelope.getRequest().getRequestId()+ ", sessionId= " + requestEnvelope.getSession().getSessionId();
  	}

  	public static final String LOG_LAUNCH_INFO(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope)
  	{
  		return "onLaunch requestId= "+ requestEnvelope.getRequest().getRequestId()+ ", sessionId= " + requestEnvelope.getSession().getSessionId();
  	}

  	public static final String LOG_INTEND_INFO(SpeechletRequestEnvelope<IntentRequest> requestEnvelope)
  	{
  		return "onIntent requestId= "+ requestEnvelope.getRequest().getRequestId()+ ", sessionId= " + requestEnvelope.getSession().getSessionId();
  	}

  	public static final String LOG_SESSION_ENDED_INFO(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope)
  	{
  		return "onSessionEnded requestId= "+ requestEnvelope.getRequest().getRequestId()+ ", sessionId= " + requestEnvelope.getSession().getSessionId();
  	}
  	// Exception log
  	public static final String EXCEPTION_SESSION_STARTED(Exception exp)
  	{
  		return "Exception Occured: "+exp;
  	}

  	public static final String EXCEPTION_INTENT(String intentName)
  	{
  		return "Unrecognized intent: " + intentName;
  	}

    // Alexa intent strings and methods
    public static final String ALEXA_WELCOME()
    {
    		return randomText(WELCOME) + STRUCT_TEAM_ID;
    }

    public static final String ALEXA_CANCEL()
    {
    	return randomText(GOODBYE);
    }

    public static final String ALEXA_STOP()
    {
		return randomText(GOODBYE);
    }

    public static final String ALEXA_DEFAULT_MEETING(Integer meetingId, Integer meetingStage)
    {
		if(meetingId==0)
		{
			return ALEXA_ERROR_NO_MEETING;
		}
		else
		{
			return " Your latest meeting, meeting id: "+ meetingId + " is selected. This meeting is at the stage of " + STAGE(meetingStage) + ". ";
		}
    }

    public static final String ALEXA_SELECT_TEAM(Integer teamId)
    {
    		return "Welcome, team " + teamId +". ";
    }

    public static final String ALEXA_SELECT_TEAM_ERROR(Integer teamId)
    {
    		return "Team " + teamId + " is not in the database, please select an existing team.";
    }

    public static final String ALEXA_NEW_MEETING(Integer meetingId, String meetingName)
    {
    		return "A new retro meeting named "+ meetingName +" with meeting id: " + meetingId +" has been created.";
    }

    public static final String ALEXA_EXIST_MEETING(Integer meetingId, Integer meetingStage)
    {
		return "An existing meeting: " + meetingId +", has been selected. This meeting is at the stage of " + STAGE(meetingStage);
    }

    public static final String ALEXA_EXIST_MEETING_ERROR(Integer meetingId)
    {
    		return "Meeting " + meetingId + " is not in the database, please select an existing meeting.";
    }

    public static final String ALEXA_QUERY_TEAM_ID(Integer teamId)
    {
		return "Your team id is " + teamId + ".";
    }

    public static final String ALEXA_QUERY_MEETING_ID(Integer meetingId)
    {
		return "The current meeting id is " + meetingId + ".";
    }

    public static final String ALEXA_ADD_RETRO_ITEM(String category, String retroItem)
    {
		return retroItem +" has been added to "+ category +".";
    }

    public static final String ALEXA_DELETE_LAST_ADDED_RETRO_ITEM(String content, String category)
    {
    		return "Your last added retro item: " + content +", has been deleted from " + category;
    }

    public static final String ALEXA_EXTEND_TIMER(int minutes)
    {
    		return "Timer extended for "+minutes + " minutes.";
    }

    public static final String ALEXA_FINISH_RETRO(Integer meetingId)
    {
    		return "Retro meeting with id " + meetingId +" is finished now, you will find the meeting details in your email attachment. Thanks everyone and see you next time.";
    }

    // Helper methods
    private static final String randomText(String[] strings)
    {
    		// Get a random welcome sentences from the welcome list
        int randomIndex = (int) Math.floor(Math.random() * strings.length);
        String selectedText = strings[randomIndex];
    		return selectedText;
    }

    //
    // Unit test strings
    //
    public static final String TEST_WELCOME(Integer dummyId)
    {
    		return "Unit Test: welcome id:" + dummyId;
    }
}
