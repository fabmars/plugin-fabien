package io.kestra.plugin.fmatest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import io.kestra.core.junit.annotations.ExecuteFlow;
import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.models.executions.Execution;
import io.kestra.core.queues.QueueException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;


@KestraTest(startRunner = true)
class FlowRunnerTest {
    @Test
    @ExecuteFlow("flows/fmatest.yaml")
    void flow(Execution execution) throws TimeoutException, QueueException {
        assertThat(execution.getTaskRunList(), hasSize(2));
    }
}
