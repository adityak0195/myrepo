package com.kb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

public class StringOccurrencesCounterService {

    @SuppressWarnings("deprecation")
    public static InfoTable program(DateTime startDate, DateTime endDate, String directoryPath) throws Exception {
        // Define the structure of the InfoTable
        DataShapeDefinition dataShapeDef = new DataShapeDefinition();
        dataShapeDef.addFieldDefinition(new FieldDefinition("Date", BaseTypes.DATETIME));
        dataShapeDef.addFieldDefinition(new FieldDefinition("Name", BaseTypes.STRING));
        dataShapeDef.addFieldDefinition(new FieldDefinition("Count", BaseTypes.INTEGER));

        // Create an InfoTable based on the defined structure
        InfoTable infoTable = InfoTableInstanceFactory.createInfoTableFromDataShape(dataShapeDef);

        // Root directory for file search
        File rootDirectory = new File("/");

        try {
            // Find the directory where the log files are stored
            String inputDirectory = findTomcatLogsDirectory(rootDirectory, directoryPath);

            // Date format for parsing log file names
            SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Initialize calendar with start date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate.toDate());

            // Adjust calendar to start reading one day ahead
            calendar.add(Calendar.DATE, 1);

            // Iterate through each date within the date range
            while (!calendar.getTime().after(endDate.toDate())) {
                String dateStr = fileDateFormat.format(calendar.getTime());

                // Construct the log file path
                String fileName = "/" + "localhost_access_log." + dateStr + ".txt";
                String filePath = inputDirectory + fileName;

                File file = new File(filePath);

                if (file.exists() && file.isFile()) {
                    // Read the log file and count occurrences of specific strings
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        Map<String, Integer> countMap = new HashMap<>();
                        String line;
                        while ((line = br.readLine()) != null) {
                            // Ignore lines containing "Theme%20Preview"
                            if (line.contains("Theme%20Preview")) {
                                continue;
                            }
                            // Find and count occurrences of "mashup=" parameter
                            int startPatternIndex = line.indexOf("mashup=");
                            if (startPatternIndex != -1) {
                                int endPatternIndex1 = line.indexOf("&appKey", startPatternIndex);
                                int endPatternIndex2 = line.indexOf("&forceClose", startPatternIndex);
                                int endPatternIndex3 = line.indexOf("&_", startPatternIndex);
                                int endIndex = Math.min(Math.min(
                                        endPatternIndex1 != -1 ? endPatternIndex1 : Integer.MAX_VALUE,
                                        endPatternIndex2 != -1 ? endPatternIndex2 : Integer.MAX_VALUE),
                                        endPatternIndex3 != -1 ? endPatternIndex3 : Integer.MAX_VALUE);

                                if (endIndex != -1 && endIndex < line.length()) {
                                    String result = line.substring(startPatternIndex + "mashup=".length(), endIndex);
                                    countMap.put(result, countMap.getOrDefault(result, 0) + 1);
                                }
                            }
                            // Find and count occurrences of "/Mashups/" path
                            int startPatternIndex2 = line.indexOf("/Mashups/");
                            int endPatternIndex2 = line.indexOf("?_", startPatternIndex2);
                            if (startPatternIndex2 != -1 && endPatternIndex2 != -1) {
                                String result2 = line.substring(startPatternIndex2 + "/Mashups/".length(), endPatternIndex2);
                                countMap.put(result2, countMap.getOrDefault(result2, 0) + 1);
                            }
                        }
                        // Add the counts to the InfoTable
                        for (Map.Entry<String, Integer> countEntry : countMap.entrySet()) {
                            ValueCollection row = new ValueCollection();
                            row.setValue("Date", new StringPrimitive(dateStr));
                            row.setValue("Name", new StringPrimitive(countEntry.getKey()));
                            row.setValue("Count", new IntegerPrimitive(countEntry.getValue()));
                            infoTable.addRow(row);
                        }
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                // Move to the next date
                calendar.add(Calendar.DATE, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoTable;
    }

    // Recursively find the directory containing Tomcat logs
    private static String findTomcatLogsDirectory(File directory, String logsDirectoryName) {
        File logsDirectory = new File(directory, logsDirectoryName);
        if (logsDirectory.exists() && logsDirectory.isDirectory()) {
            return logsDirectory.getAbsolutePath();
        }

        File[] subDirectories = directory.listFiles(File::isDirectory);
        if (subDirectories != null) {
            for (File subDirectory : subDirectories) {
                String logsDir = findTomcatLogsDirectory(subDirectory, logsDirectoryName);
                if (logsDir != null) {
                    return logsDir;
                }
            }
        }
        return null;
    }
}
