package com.smartbear.readyapi4j.execution;

import com.smartbear.readyapi.client.model.ProjectResultReport;
import com.smartbear.readyapi.client.model.TestCaseResultReport;
import com.smartbear.readyapi.client.model.TestSuiteResultReport;
import com.smartbear.readyapi4j.extractor.ExtractorData;
import com.smartbear.readyapi4j.extractor.ExtractorOperator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class to execute the data extractions after recipe execution.
 */
public class DataExtractors {
    public static void runDataExtractors(ProjectResultReport executionStatus, List<ExtractorData> extractorDataList) {
        List<String> extractorDataIdList = extractorDataList
                .stream()
                .map(ExtractorData::getExtractorDataId)
                .collect(Collectors.toList());
        Optional<TestCaseResultReport> resultReport = executionStatus
                .getTestSuiteResultReports()
                .stream()
                .map(TestSuiteResultReport::getTestCaseResultReports)
                .flatMap(Collection::stream)
                .filter(testCaseResultReport ->
                        extractorDataIdList.contains(testCaseResultReport.getProperties().get(ExtractorData.EXTRACTOR_DATA_KEY)))
                .findAny();

        resultReport.ifPresent(testCaseResultReport -> {
            Map<String, String> properties = testCaseResultReport.getProperties();
            runExtractorFunctions(extractorDataList, properties);

            // After run, remove all unnecessary properties
            properties.entrySet().removeIf(entry -> entry.getKey().contains(properties.get(ExtractorData.EXTRACTOR_DATA_KEY)));
            properties.remove(ExtractorData.EXTRACTOR_DATA_KEY);
        });
    }


    private static void runExtractorFunctions(List<ExtractorData> extractorDataList, Map<String, String> properties) {
        Optional<ExtractorData> extractorData = extractorDataList
                .stream()
                .filter(ed -> ed.getExtractorDataId().equals(properties.get(ExtractorData.EXTRACTOR_DATA_KEY)))
                .findAny();
        extractorData.ifPresent(extractorData1 -> properties.forEach((key, value) -> {
            ExtractorOperator operator = extractorData1.getExtractorOperator(key);
            if (operator != null) {
                operator.extractValue(value);
            }
        }));
    }
}
