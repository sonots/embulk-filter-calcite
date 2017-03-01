package org.embulk.filter.calcite;

import com.google.common.collect.ImmutableList;
import org.embulk.config.ConfigSource;
import org.embulk.spi.FilterPlugin;
import org.embulk.test.TestingEmbulk;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.embulk.test.EmbulkTests.copyResource;
import static org.embulk.test.EmbulkTests.readResource;
import static org.embulk.test.EmbulkTests.readSortedFile;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TestCalciteFilterPlugin
{
    private static final String RESOURCE_NAME_PREFIX = "org/embulk/filter/calcite/test/";

    @Rule
    public TestingEmbulk embulk = TestingEmbulk.builder()
            .registerPlugin(FilterPlugin.class, "calcite", CalciteFilterPlugin.class)
            .build();

    private ConfigSource baseConfig;

    @Before
    public void setup()
    {
        baseConfig = embulk.newConfig();
    }

    @Test
    public void testSimple() throws Exception
    {
        assertRecordsByResource(embulk, "test_simple_in.yml", "test_simple_filter.yml",
                "test_simple_source.csv", "test_simple_expected.csv");
    }

    static void assertRecordsByResource(TestingEmbulk embulk,
            String inConfigYamlResourceName, String filterConfigYamlResourceName,
            String sourceCsvResourceName, String resultCsvResourceName)
            throws IOException
    {
        Path inputPath = embulk.createTempFile("csv");
        Path outputPath = embulk.createTempFile("csv");

        // in: config
        copyResource(RESOURCE_NAME_PREFIX + sourceCsvResourceName, inputPath);
        ConfigSource inConfig = embulk.loadYamlResource(RESOURCE_NAME_PREFIX + inConfigYamlResourceName)
                .set("path_prefix", inputPath.toAbsolutePath().toString());

        // remove_columns filter config
        ConfigSource filterConfig = embulk.loadYamlResource(RESOURCE_NAME_PREFIX + filterConfigYamlResourceName);

        TestingEmbulk.RunResult result = embulk.inputBuilder()
                .in(inConfig)
                .filters(ImmutableList.of(filterConfig))
                .outputPath(outputPath)
                .run();

        assertThat(readSortedFile(outputPath), is(readResource(RESOURCE_NAME_PREFIX + resultCsvResourceName)));
    }
}
