package com.danny.alexa;

import java.util.HashSet;
import java.util.Set;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public final class RequestHandler extends SpeechletRequestStreamHandler
{
	private static final Set<String> supportedApplicationIds = new HashSet<String>();

	static
	{
		//supportedApplicationIds.add("");
	}

	public RequestHandler()
	{
	    super(new Speechlet(), supportedApplicationIds);
	}
}
