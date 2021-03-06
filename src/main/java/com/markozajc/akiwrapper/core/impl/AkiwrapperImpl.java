package com.markozajc.akiwrapper.core.impl;

import java.util.*;

import javax.annotation.*;

import org.json.*;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.*;
import com.markozajc.akiwrapper.core.exceptions.*;

public class AkiwrapperImpl implements Akiwrapper {

	private static final String NO_MORE_QUESTIONS_STATUS = "elem list is empty";
	private static final String PARAMETERS_KEY = "parameters";

	public static class Token {

		private static final String AUTH_QUERYSTRING = "&session=%s&signature=%s";

		private final long signature;
		private final int session;

		public Token(long signature, int session) {
			this.signature = signature;
			this.session = session;
		}

		private long getSignature() {
			return this.signature;
		}

		private int getSession() {
			return this.session;
		}

		public String compile() {
			return String.format(AUTH_QUERYSTRING, "" + this.getSession(), "" + this.getSignature());
		}

	}

	@Nonnull
	private final Server server;
	private final boolean filterProfanity;
	@Nonnull
	private final Token token;
	@Nonnegative
	private int currentStep;
	@Nullable
	private Question currentQuestion;

	@SuppressWarnings("null")
	public AkiwrapperImpl(@Nonnull Server server, boolean filterProfanity) {
		JSONObject question = Route.NEW_SESSION
			.getRequest("", filterProfanity, Long.toString(System.currentTimeMillis()), server.getUrl())
			.getJSON();
		JSONObject parameters = question.getJSONObject(PARAMETERS_KEY);

		this.token = getToken(parameters);
		this.currentQuestion = new QuestionImpl(parameters.getJSONObject("step_information"), new StatusImpl("OK"));
		this.filterProfanity = filterProfanity;
		this.server = server;
		this.currentStep = 0;
	}

	@Nonnull
	private static Token getToken(@Nonnull JSONObject parameters) {
		JSONObject identification = parameters.getJSONObject("identification");
		return new Token(Long.parseLong(identification.getString("signature")),
						 Integer.parseInt(identification.getString("session")));
	}

	@SuppressWarnings("null")
	@Override
	public Question answerCurrentQuestion(Answer answer) {
		Question currentQuestion2 = this.currentQuestion;
		if (currentQuestion2 != null) {
			JSONObject question = Route.ANSWER
				.getRequest(this.server.getUrl(), this.filterProfanity, this.token, "" + currentQuestion2.getStep(),
							"" + answer.getId())
				.getJSON();
			try {
				this.currentQuestion =
					new QuestionImpl(question.getJSONObject(PARAMETERS_KEY), new StatusImpl(question));
			} catch (MissingQuestionException e) { // NOSONAR It does not need to be logged
				this.currentQuestion = null;
				return null;
			}

			this.currentStep += 1;
			return this.currentQuestion;
		}

		return null;
	}

	@SuppressWarnings("null")
	@Override
	public Question undoAnswer() {
		Question current = getCurrentQuestion();
		if (current == null)
			return null;

		if (current.getStep() < 1)
			return null;

		JSONObject question = Route.CANCEL_ANSWER
			.getRequest(this.server.getUrl(), this.filterProfanity, this.token, Integer.toString(current.getStep()))
			.getJSON();

		this.currentQuestion = new QuestionImpl(question.getJSONObject(PARAMETERS_KEY), new StatusImpl(question));

		this.currentStep -= 1;
		return this.currentQuestion;
	}

	@Override
	public Question getCurrentQuestion() {
		return this.currentQuestion;
	}

	@SuppressWarnings("null")
	@Override
	public List<Guess> getGuesses() {
		JSONObject list = null;
		try {
			list = Route.LIST.getRequest(this.server.getUrl(), this.filterProfanity, this.token, "" + this.currentStep)
				.getJSON();
		} catch (StatusException e) {
			if (e.getStatus().getLevel() == Level.ERROR
				&& NO_MORE_QUESTIONS_STATUS.equalsIgnoreCase(e.getStatus().getReason())) {
				return Collections.emptyList();
			}

			throw e;
		}

		JSONArray elements = list.getJSONObject(PARAMETERS_KEY).getJSONArray("elements");
		List<Guess> guesses = new ArrayList<>();
		for (int i = 0; i < elements.length(); i++)
			guesses.add(new GuessImpl(elements.getJSONObject(i).getJSONObject("element")));
		// Currently the only way to (cleanly) extract JSONObjects from a JSONArray
		// without having to box and unbox it a million times is to use this old (and
		// ugly) but gold, condition-based for loop :P

		return Collections.unmodifiableList(guesses);
	}

	@Override
	public Server getServer() {
		return this.server;
	}

}
