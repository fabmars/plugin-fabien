package io.kestra.plugin.fmatest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.models.property.Property;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import jakarta.inject.Inject;
import java.net.URL;
import java.util.Map;
import org.junit.jupiter.api.Test;

@KestraTest
class WireFormatWriteTest {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void run() throws Exception {
        // ok ressemble à spring batch

        URL resource = WireFormatWriteTest.class.getClassLoader().getResource("data/wires.lst");
        RunContext runContext = runContextFactory.of(Map.of("uri", resource.toURI().toString()));

        WireFormatWrite task = WireFormatWrite.builder()
            .from(new Property<>("{{uri}}")) // no idea what I'm doing here
            .build();

        WireFormatWrite.Output runOutput = task.run(runContext);

        assertEquals(2, runOutput.getCount());
    }
}
