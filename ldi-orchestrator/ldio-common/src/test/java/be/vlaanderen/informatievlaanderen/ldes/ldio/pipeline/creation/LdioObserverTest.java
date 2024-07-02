package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LdioObserverTest {
	private static final String COMPONENT_NAME = "Ldio:ComponentName";
	private static final String PIPELINE_NAME = "pipeline-name";
	private LdioObserver ldioObserver;

	@BeforeEach
	void setUp() {
		ldioObserver = LdioObserver.register(COMPONENT_NAME, PIPELINE_NAME, null);
	}

	@Test
	void when_ObservableDoesNotThrowException_then_DoNotLog() {
		final Supplier<String> additionalLogSupplier = mock();
		final Runnable observable = () -> {};

		ldioObserver.observe(observable, "test", additionalLogSupplier);

		verifyNoInteractions(additionalLogSupplier);
	}

	@Test
	void when_ObservableDoesThrowsException_then_Log() {
		final Supplier<String> additionalLogSupplier = mock();
		final Runnable observable = () -> {
			throw new RuntimeException();
		};

		assertThatThrownBy(() -> ldioObserver.observe(observable, "test", additionalLogSupplier))
				.isInstanceOf(RuntimeException.class);

		verify(additionalLogSupplier).get();
	}
}