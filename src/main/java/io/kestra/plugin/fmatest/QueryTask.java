package io.kestra.plugin.fmatest;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.common.FetchType;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    title = "QueryTask",
    description = "Query Task"
)
@Plugin(
    examples = {
        @io.kestra.core.models.annotations.Example(
            title = "QueryTask",
            code = { "fetchType: \"the fetch type to use\"" }
        )
    }
)

public class QueryTask {
    @NotNull
    @Schema(
        title = "Fetch type"
    )
    private Property<String> fetchType;


    public Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();
        FetchType fetchType = runContext.render(this.fetchType)
            .as(String.class)
            .map(FetchType::valueOf)
            .orElseThrow();


        InfluxDBClient influxDBClient = InfluxFactory.influxDBClient();
        String flux = "from(bucket:\"%s\") |> range(start: 0)".formatted(InfluxFactory.bucket);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<String> lines = new ArrayList<>();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();

            for (FluxRecord fluxRecord : records) {
                String line = (String)fluxRecord.getValueByKey("_value");
                logger.info(fluxRecord.getTime() + ": " + line);
                lines.add(line);
            }
        }
        influxDBClient.close();

        return switch (fetchType) {
            case STORE -> {
                File storedFile = File.createTempFile("query", ""); // sorry for your loss
                URI uri = runContext.storage().putFile(storedFile);
                yield Output.ofUri(uri);
            }
            case FETCH -> {
                yield Output.ofRawData(lines);
            }
            default -> throw new UnsupportedOperationException("Unsupported fetch type: " + fetchType);
        };
    }




    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {
        @Schema(title = "The fetched lines.")
        private final FetchType fetchType;
        private final List<String> lines;
        private final URI uri;

        private Output(FetchType fetchType, List<String> lines, URI uri) {
            this.fetchType = fetchType;
            this.lines = lines;
            this.uri = uri;
        }

        public static Output ofRawData(List<String> lines) {
          return new Output(FetchType.FETCH, Collections.unmodifiableList(lines), null);
        }

        public static Output ofUri(URI url) {
            return new Output(FetchType.STORE, null, url);
        }
    }
}
