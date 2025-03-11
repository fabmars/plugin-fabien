package io.kestra.plugin.fmatest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.common.FetchType;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import jakarta.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@KestraTest
class QueryTest {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void should_query_with_store() throws Exception {
        // Given
        WireFormatWriteTest.execWrite(runContextFactory);

        // When
        RunContext runContext = runContextFactory.of(Map.of("fetchType", FetchType.STORE.name()));

        QueryTask task = QueryTask.builder()
            .fetchType(new Property<>("{{fetchType}}")) // no idea what I'm doing here
            .build();

        QueryTask.Output output = task.run(runContext);

        assertEquals(FetchType.STORE, output.getFetchType());
        assertNull(output.getLines());
        assertNotNull(output.getUri());
        Path tmpFile = Paths.get(output.getUri());
        assertTrue(Files.exists(tmpFile));
    }

    @Test
    void should_query_with_fetch() throws Exception {
        RunContext runContext = runContextFactory.of(Map.of("fetchType", FetchType.FETCH.name()));

        QueryTask task = QueryTask.builder()
            .fetchType(new Property<>("{{fetchType}}")) // no idea what I'm doing here
            .build();

        QueryTask.Output output = task.run(runContext);

        assertEquals(FetchType.FETCH, output.getFetchType());
        assertNull(output.getUri());
        List<String> lines = output.getLines();
        assertNotNull(lines);
        //TODO test what's out
    }

    @Test
    void should_query_with_unsupported_type() {
        RunContext runContext = runContextFactory.of(Map.of("fetchType", FetchType.FETCH_ONE.name()));

        QueryTask task = QueryTask.builder()
            .fetchType(new Property<>("{{fetchType}}")) // no idea what I'm doing here
            .build();

        assertThrows(UnsupportedOperationException.class, () -> task.run(runContext));
    }
}
