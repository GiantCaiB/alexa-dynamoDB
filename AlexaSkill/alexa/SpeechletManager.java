package com.danny.alexa;

import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.danny.base.Meeting;
import com.danny.custom.Controller;
import com.danny.custom.TeamDetails;
import com.danny.pdf.GeneratePDF;
import com.danny.util.TextUtils;

public class SpeechletManager
{

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	public SpeechletResponse getWelcomeResponse(Session session)
	{
	    return this.getAskResponse(TextUtils.CARD_WELCOME, TextUtils.ALEXA_WELCOME());
	}

	/**
	 * Creates a {@code SpeechletResponse} for the SelectTeam Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getSelectTeamResponse(final Intent intent, final Session session)
	{
		String speechText = null;
		Integer teamId = null;
		try
		{
			// Can only select team once unless restart the skill
			if(session.getAttribute(TextUtils.KEY_TEAM_ID) == null)
			{
				// Get user input from slots
				Map<String, Slot> slots = intent.getSlots();
				teamId = Integer.parseInt(slots.get(TextUtils.SLOT_TEAM_ID).getValue());
				// Team validation
				TeamDetails.getTeamDetails(teamId);
    				// Get meeting by id, this may throw exception
    				Meeting meeting = Controller.getLatestMeetingByTeamId(teamId);
				// Meeting could be null if 1) team does not exist; 2) no meeting under this team
				speechText = TextUtils.ALEXA_SELECT_TEAM(teamId);
				// ! when meeting is null, meeting.getMeetingID() will crash the skill
				session.setAttribute(TextUtils.KEY_MEETING_ID, meeting.getMeetingID());
				speechText += TextUtils.ALEXA_DEFAULT_MEETING(meeting.getMeetingID(), meeting.getStage());
			}
			// Restart the skill to select team
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_NO_TEAM_CHANGING);
			}
		}
		catch (Exception exp)
		{
			// No such team
 			if(exp.getMessage().equals(TextUtils.CTRL_EXP_TEAM_INVALID))
 			{
 				return this.getErrorInfoResponse(TextUtils.ALEXA_SELECT_TEAM_ERROR(teamId));
 			}
 			// No meeting under this team
 			else if(exp.getMessage().equals(TextUtils.CTRL_EXP_MEETING_ID))
 			{
 				speechText = TextUtils.ALEXA_SELECT_TEAM(teamId);
 				speechText += TextUtils.ALEXA_ERROR_NO_MEETING;
 			}
 			else
 			{
 				// Invalid input from user
 				return this.getErrorInfoResponse(exp.getMessage());
 			}
		}
		// Set the team id in session
		session.setAttribute(TextUtils.KEY_TEAM_ID, teamId);
		return this.getAskResponse(TextUtils.CARD_TEAM_SELECTED, speechText);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the StartNewMeeting Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getStartNewMeetingResponse(final Intent intent, final Session session)
	{
		try
		{
			// Must have a team selected when creating a new meeting
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				// Get team id from the session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				// Get user input value for the meeting name from slots
				Map<String, Slot> slots = intent.getSlots();
				String meetingName = (String)slots.get(TextUtils.SLOT_MEETING_NAME).getValue();
				// Generate a new meeting with an id from DynamoDB
				Meeting meeting = new Meeting();
				meeting.setTeamID(teamId);
				meeting.setMeetingName(meetingName);
				Integer meetingId = Controller.createMeeting(meeting);
				// Reset the record
				session.setAttribute(TextUtils.KEY_MEETING_ID, meetingId);
				session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_CAT, null);
				session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_ID, null);
				session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM, null);
				// Response to user from Alexa
				String speechText = TextUtils.ALEXA_NEW_MEETING(meetingId, meetingName);
				return this.getAskResponse(TextUtils.CARD_MEETING_NEW,speechText);
			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch (Exception exp)
		{
			return this.getErrorInfoResponse(exp.getMessage());
		}
	}

	/**
	 * Creates a {@code SpeechletResponse} for the ContinueExistMeeting Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getContinueExistMeetingResonse(final Intent intent, final Session session)
	{
		String speechText = null;
		Integer meetingId = null;
		try
		{
		 	// Must have a team selected when selecting an existing meeting
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				// Get team id from the session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				// Get user input value for meeting id from slots
				Map<String, Slot> slots = intent.getSlots();
				meetingId = Integer.parseInt(slots.get(TextUtils.SLOT_MEETING_ID).getValue());
				// Get the existing meeting by id
				Meeting meeting = Controller.getMeetingByID(teamId, meetingId);
				// Reset the record
				session.setAttribute(TextUtils.KEY_MEETING_ID, meetingId);
				session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_CAT, null);
				session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_ID, null);
				session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM, null);
				// Response text to user
				speechText =  TextUtils.ALEXA_EXIST_MEETING(meetingId,  meeting.getStage());
			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch (Exception exp)
		{
			// No such meeting id in the database
			if(meetingId != null)
			{
				speechText = TextUtils.ALEXA_EXIST_MEETING_ERROR(meetingId);
			}
			// Invalid input from user
			else
			{
				return this.getErrorInfoResponse(exp.getMessage());
			}
		}
		return this.getAskResponse(TextUtils.CARD_MEETING_CONTINUE,speechText);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the QueryTeamId Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getQueryTeamIdResonse(final Session session)
	{
		String speechText = null;
		try
		{
			// Must have a team selected when querying a team
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				// Get current team id from session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				speechText = TextUtils.ALEXA_QUERY_TEAM_ID(teamId);
			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch (Exception exp)
		{
			return this.getErrorInfoResponse(exp.getMessage());
		}
		return this.getAskResponse(TextUtils.CARD_TEAM_QUERY,speechText);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the QueryMeetingId Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getQueryMeetingIdResonse(final Session session)
	{
		String speechText = null;
		try
		{
			// Must have a team selected when querying a meeting
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				// Must have a meeting selected when querying a meeting
				if(session.getAttribute(TextUtils.KEY_MEETING_ID)!=null)
				{
					// Get current meeting id from session
					Integer meetingId = (Integer) session.getAttribute(TextUtils.KEY_MEETING_ID);
					speechText = TextUtils.ALEXA_QUERY_MEETING_ID(meetingId);
				}
				// Must select a meeting first
				else
				{
					return this.getErrorInfoResponse(TextUtils.STRUCT_MEETING_ID);
				}
			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch (Exception exp)
		{
			return this.getErrorInfoResponse(exp.getMessage());
		}
		return this.getAskResponse(TextUtils.CARD_MEETING_QUERY,speechText);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the  AddGood/BadItem Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getAddItemResponse(final Intent intent, final Session session, String category)
	{
		try
		{
			// Must have a team selected when adding an item
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				String speechText = null;
				// Get the team and meeting id from session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				Integer meetingId = (Integer) session.getAttribute(TextUtils.KEY_MEETING_ID);
				// If there is no meeting in DB, user cannot add anything
				if(meetingId == 0 || meetingId == null)
				{
					return this.getErrorInfoResponse(TextUtils.STRUCT_MEETING_ID);
				}
				else
				{
					// Get the existing meeting by id
					Meeting meeting = Controller.getMeetingByID(teamId, meetingId);
					// Must at the stage: adding
					if(meeting.getStage() == TextUtils.STAGE_ADDING)
					{
						// Get retro item value from the slot
						String retroItem = intent.getSlot(TextUtils.SLOT_RETRO_ITEM).getValue();
						// Pass to backend for adding in DynamoDB
						Integer itemId = Controller.addRetroItem(teamId, meetingId, category, retroItem, null).getRetroItemId();
						// Last added item record for deleting purpose
						session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_CAT, category);
						session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_ID, itemId);
						session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM, retroItem);
						// Response to user from Alexa
						speechText = TextUtils.ALEXA_ADD_RETRO_ITEM(category,retroItem);
					    return this.getAskResponse(TextUtils.CARD_ITEM_ADDED, speechText);
					}
					// Give user a warning of stage status
					else
					{
						return this.getErrorInfoResponse(TextUtils.STRUCT_STAGE_REQUIRED(TextUtils.STAGE_ADDING));
					}
				}
			}
			// A team must be selected first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch (Exception exp)
		{
			return this.getErrorInfoResponse(exp.getMessage());
		}
	}

	/**
	 * Creates a {@code SpeechletResponse} for the  DeleteLastAddedItem Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getDeleteLastAddedItemResponse(final Session session)
	{
		try
		{
			// Must have a team selected when deleting an item
			if(session.getAttribute(TextUtils.KEY_TEAM_ID) != null)
			{
				String speechText = null;
				// Getting values of current team id, meeting id, last added item's category and last added item id from session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				Integer meetingId = (Integer) session.getAttribute(TextUtils.KEY_MEETING_ID);
				String category = (String) session.getAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_CAT);
				Integer itemId = (Integer) session.getAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_ID);
				String retroItem = (String) session.getAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM);
				// Get the existing meeting by id
				Meeting meeting = Controller.getMeetingByID(teamId, meetingId);
				// Must at the stage: adding
				if(meeting.getStage() == TextUtils.STAGE_ADDING)
				{
					// Check last added item record
					if(meetingId != 0 && category != null && itemId != null)
					{
						// Pass to the backend for deleting from DynamoDB
						String itemContent = Controller.deleteRetroItem(teamId, meetingId, category, itemId, retroItem);
						// Clean the record
						session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM_ID, null);
						session.setAttribute(TextUtils.KEY_LAST_ADDED_RETRO_ITEM, null);
						// Set response text
						speechText = TextUtils.ALEXA_DELETE_LAST_ADDED_RETRO_ITEM(itemContent, category);
					}
					else
					{
						// Added something already
						if(category!=null)
						{
							speechText = TextUtils.ALEXA_ERROR_NO_LAST_ADDED_ITEM;
						}
						// Haven't added anything yet
						else
						{
							speechText = TextUtils.ALEXA_ERROR_NO_ADDED_ITEM;
						}
					}
					// Response to user from Alexa
					return this.getAskResponse(TextUtils.CARD_ITEM_DELETED, speechText);
				}
				// Give user a warning of stage status
				else
				{
					return this.getErrorInfoResponse(TextUtils.STRUCT_STAGE_REQUIRED(TextUtils.STAGE_ADDING));
				}
			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch (Exception exp)
		{
			return this.getErrorInfoResponse(exp.getMessage());
		}
	}

	/**
	 * Creates a {@code SpeechletResponse} for the  StartTimer Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getExtendTimerResponse(final Intent intent, final Session session)
	{
		try
		{
			// Must have a team selected when extending timer
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				// Getting values of current team id, meeting id from session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				Integer meetingId = (Integer) session.getAttribute(TextUtils.KEY_MEETING_ID);
				// Get the existing meeting by id
				Meeting meeting = Controller.getMeetingByID(teamId, meetingId);
				// Must at the stage: adding
				if(meeting.getStage() == TextUtils.STAGE_DISCUSSION)
				{
					String speechText = null;
					// Get the slots from the intent.
			        Map<String, Slot> slots = intent.getSlots();
					// Get value from user input: timer extend duration
					String duration_ISO8601 = slots.get(TextUtils.SLOT_DURATION).getValue();
					// ISO 8601 Time Interval Parsing
					Duration duration = DatatypeFactory.newInstance().newDuration(duration_ISO8601);
					// int hours = duration.getHours();
					int minutes = duration.getMinutes();
					// int seconds = duration.getSeconds();
					// TODO Send the extension to the web app via DB
					speechText = TextUtils.ALEXA_EXTEND_TIMER(minutes);
				    return this.getAskResponse(TextUtils.CARD_TIMER_EXTENDED, speechText);
				}
				// Give user a warning of stage status
				else
				{
					return this.getErrorInfoResponse(TextUtils.STRUCT_STAGE_REQUIRED(TextUtils.STAGE_DISCUSSION));
				}

			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch(Exception exp)
		{
			return this.getErrorInfoResponse(exp.getMessage());
		}
	}

	/**
	 * Creates a {@code SpeechletResponse} for the  FinishRetro Intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws Exception
	 */
	public SpeechletResponse getFinishRetroResponse(final Session session)
	{
		try
		{
			// Must have a team selected when finishing retro
			if(session.getAttribute(TextUtils.KEY_TEAM_ID)!=null)
			{
				// Getting values of current team id, meeting id from session
				Integer teamId = (Integer) session.getAttribute(TextUtils.KEY_TEAM_ID);
				Integer meetingId = (Integer) session.getAttribute(TextUtils.KEY_MEETING_ID);
				// Get the existing meeting by id
				Meeting meeting = Controller.getMeetingByID(teamId, meetingId);
				// Must at the stage: adding
				if(meeting.getStage() == TextUtils.STAGE_DISCUSSION)
				{
					String speechText = null;
					// Process to summarise all retro items in email and send it to all attenders
					if(GeneratePDF.generatePDF(meeting))
					{
						speechText = TextUtils.ALEXA_FINISH_RETRO(meetingId);
					}
					else
					{
						speechText = TextUtils.ALEXA_ERROR_INFO;
					}
					return this.getTellResponse(TextUtils.CARD_RETRO_FINISHED, speechText);
				}
				// Give user a warning of stage status
				else
				{
					return this.getErrorInfoResponse(TextUtils.STRUCT_STAGE_REQUIRED(TextUtils.STAGE_DISCUSSION));
				}
			}
			// Must select an existing team first
			else
			{
				return this.getErrorInfoResponse(TextUtils.STRUCT_TEAM_ID);
			}
		}
		catch(Exception exp)
		{
			// Auto-generated catch block
			return this.getErrorInfoResponse(exp.getMessage());
		}
	}

	/**
	 * Creates a {@code SpeechletResponse} for the cancel intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	public SpeechletResponse getCancelResponse()
	{
	    return this.getTellResponse(TextUtils.CARD_CANCEL,  TextUtils.ALEXA_CANCEL());
	}

	/**
	 * Creates a {@code SpeechletResponse} for the help intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	public SpeechletResponse getHelpResponse(boolean unrecognisedInput)
	{
		String speechText = null;
		// When not recognizing user's input
		if(unrecognisedInput)
		{
			speechText = TextUtils.ALEXA_DEFAULT_FALLBACK_INTENT;
		}
		// Normal help intent
		else
		{
			speechText = TextUtils.ALEXA_HELP;
		}
		return this.getAskResponse(TextUtils.CARD_HELP, speechText);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the stop intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	public SpeechletResponse getStopResponse()
	{
	    return this.getTellResponse(TextUtils.CARD_STOP, TextUtils.ALEXA_STOP());
	}

	/**
 	 * Creates and returns a {@code SpeechletResponse} with a meeting selected needed message.
 	 *
 	 * @return SpeechletResponse spoken and visual response for the given intent
 	 */
 	public SpeechletResponse getErrorInfoResponse()
 	{
 	    return this.getAskResponse(TextUtils.CARD_ERROR, TextUtils.ALEXA_ERROR_INFO);
 	}

 	/**
 	 * Creates and returns a {@code SpeechletResponse} with a meeting selected needed message.
 	 *
 	 * @return SpeechletResponse spoken and visual response for the given intent
 	 */
 	public SpeechletResponse getErrorInfoResponse(String warning)
 	{
 	    return this.getAskResponse(TextUtils.CARD_ERROR, warning);
 	}
	/**
	 * Helper method that creates a card object.
	 * @param title title of the card
	 * @param content body of the card
	 * @return SimpleCard the display card to be sent along with the voice response.
	 */
	private SimpleCard getSimpleCard(String title, String content)
	{
	    SimpleCard card = new SimpleCard();
	    card.setTitle(title);
	    card.setContent(content);
	    return card;
	}

	/**
	 * Helper method for retrieving an OutputSpeech object when given a string of TTS.
	 * @param speechText the text that should be spoken out to the user.
	 * @return an instance of SpeechOutput.
	 */
	private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText)
	{
	    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	    speech.setText(speechText);
	    return speech;
	}

	/**
	 * Helper method that returns a reprompt object. This is used in Ask responses where you want
	 * the user to be able to respond to your speech.
	 * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
	 * @return Reprompt instance.
	 */
	private Reprompt getReprompt(OutputSpeech outputSpeech)
	{
	    Reprompt reprompt = new Reprompt();
	    reprompt.setOutputSpeech(outputSpeech);
	    return reprompt;
	}

	/**
	 * Helper method for retrieving an Ask response with a simple card and reprompt included.
	 * @param cardTitle Title of the card that you want displayed.
	 * @param speechText speech text that will be spoken to the user.
	 * @return the resulting card and speech text.
	 */
	private SpeechletResponse getAskResponse(String cardTitle, String speechText)
	{
	    return SpeechletResponse.newAskResponse(this.getPlainTextOutputSpeech(speechText), this.getReprompt(this.getPlainTextOutputSpeech(speechText)), this.getSimpleCard(cardTitle, speechText));
	}

	/**
	 * Helper method for retrieving an Tell response with a simple card included.
	 * @param cardTitle Title of the card that you want displayed.
	 * @param speechText speech text that will be spoken to the user.
	 * @return the resulting card and speech text.
	 */
	private SpeechletResponse getTellResponse(String cardTitle, String speechText)
	{
	    return SpeechletResponse.newTellResponse(this.getPlainTextOutputSpeech(speechText), this.getSimpleCard(cardTitle, speechText));
	}
}
