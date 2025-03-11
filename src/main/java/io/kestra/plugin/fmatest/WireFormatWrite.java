package io.kestra.plugin.fmatest;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.property.Property;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "WireFormatWrite",
    description = "Wire Format Write"
)
@Plugin(
    examples = {
        @io.kestra.core.models.annotations.Example(
            title = "WireFormatWrite",
            code = { "uri: \"URI to the input file\"" }
        )
    }
)

// https://github.com/kestra-io/plugin-jdbc/blob/master/plugin-jdbc/src/main/java/io/kestra/plugin/jdbc/AbstractJdbcBatch.java
// https://github.com/kestra-io/plugin-jdbc/blob/master/plugin-jdbc-mysql/src/main/java/io/kestra/plugin/jdbc/mysql/Batch.java
public class WireFormatWrite {
    @NotNull
    @io.swagger.v3.oas.annotations.media.Schema(
        title = "Source file URI"
    )
    private Property<String> from;


    public Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();
        String uri = runContext.render(this.from).as(String.class).orElseThrow();
        URL url = URI.create(uri).toURL();


        InfluxDBClient influxDBClient = InfluxFactory.influxDBClient();
        int count = 0;
        try (WriteApi writeApi = influxDBClient.makeWriteApi(); BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while((line = br.readLine()) != null) {
                writeApi.writeRecord(WritePrecision.NS, line);
                count++;
            }
        }
        influxDBClient.close();

        return Output.builder()
            .count(count)
            .build();
    }


    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(title = "The rows count.")
        private final Integer count;
    }
}
