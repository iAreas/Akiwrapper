package com.markozajc.akiwrapper;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.impl.mutable.MutableAkiwrapperMetadata;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl;

/**
 * A class used for building a new Akinator object.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder extends MutableAkiwrapperMetadata {

	/**
	 * Creates a new AkiwrapperBuilder object. The default server used is the first
	 * available server. If a value is not changed, a constant default from
	 * {@link AkiwrapperMetadata} is used.
	 */
	public AkiwrapperBuilder() {
		super(AkiwrapperMetadata.DEFAULT_NAME, null, AkiwrapperMetadata.DEFAULT_FILTER_PROFANITY,
		    AkiwrapperMetadata.DEFAULT_LOCALIZATION, AkiwrapperMetadata.DEFAULT_GUESS_TYPE);
	}

	@Override
	public AkiwrapperBuilder setName(String name) {
		super.setName(name);

		return this;
	}

	@Override
	public AkiwrapperBuilder setServer(Server server) {
		super.setServer(server);

		return this;
	}

	@Override
	public AkiwrapperBuilder setFilterProfanity(boolean filterProfanity) {
		super.setFilterProfanity(filterProfanity);

		return this;
	}

	@Override
	public AkiwrapperBuilder setLanguage(Language localization) {
		super.setLanguage(localization);

		return this;
	}

	@Override
	public AkiwrapperBuilder setGuessType(GuessType guessType) {
		super.setGuessType(guessType);

		return this;
	}

	/**
	 * @return a new {@link Akiwrapper} instance that will use all set preferences
	 *
	 * @throws ServerNotFoundException
	 *             if no servers with that {@link Language} and {@link GuessType} is
	 *             available.
	 */
	@Nonnull
	public Akiwrapper build() {
		return new AkiwrapperImpl(this);
	}

}
