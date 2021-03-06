package com.markozajc.akiwrapper.core.entities.impl.immutable;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.params.provider.EnumSource.Mode;

import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.Status.Level;

import static org.junit.jupiter.api.Assertions.*;

class StatusImplTest {

	@ParameterizedTest
	@EnumSource(
		value = Level.class,
		mode = Mode.EXCLUDE)
	void testStringConstructorNoReason(@Nonnull Level level) {
		@SuppressWarnings("null")
		Status status = new StatusImpl(level.toString());
		assertEquals(level, status.getLevel());
		assertNull(status.getReason());
	}

	@ParameterizedTest
	@MethodSource("generateTestStringConstructorWithReason")
	void testStringConstructorWithReason(@Nonnull Level level, @Nonnull String reason) {
		String completion = level.toString() + " - " + reason;
		Status status = new StatusImpl(completion);
		assertEquals(level, status.getLevel());
		assertEquals(reason, status.getReason());
	}

	private static Stream<Arguments> generateTestStringConstructorWithReason() {
		String[] reasons = { "", "reason", "reason with spaces", "UPPERCASE", "UPPERCASE WITH SPACES" };
		Arguments[] arguments = new Arguments[Level.values().length * reasons.length];
		int i = 0;
		for (Level level : Level.values())
			for (String reason : reasons) {
				arguments[i] = Arguments.of(level, reason);
				i++;
			}
		return Stream.of(arguments);
	}

}
