import java.util.List;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.text.DecimalFormat;

public class WriterCSV {
    private final List<Test> tests;
    private final String filePath, applicationVersion;
    private final DecimalFormat df;

    public WriterCSV(List<Test> tests, String filePath, DecimalFormat df, String applicationVersion) {
        this.tests = tests;
        this.filePath = filePath;
        this.df = df;
        this.applicationVersion = applicationVersion;
    }

    /* [OLD BUT COULD BE USEFUL]
        public void writeResultsToCSV() {
            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                writer.writeNext(new String[] {"Test Name", "Passed", "Score"} );

                for (Test test : tests) {
                    String[] data = { test.getTestName(), String.valueOf(test.isPassed()), String.valueOf(df.format(test.getTestScore())) };
                    writer.writeNext(data);
                }

                System.out.println("Result.csv file updated successfully! -> " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    */

    public void writeResultsToCSV() throws IOException, CsvException {
        CSVReader csvReader = new CSVReader(new FileReader(filePath));
        List<String[]> lines = csvReader.readAll();
        csvReader.close();

        int versionColumnIndex = getVersionColumnIndex(lines.get(0), applicationVersion);
        int scoreColumnIndex   = getScoreColumnIndex(lines.get(0));

        // Writing data starting from the second row
        for (int i = 1; i < lines.size(); i++) {
            String[] row = lines.get(i);
            Test test = tests.get(i - 1);

            /* Update or Add data to the specified columns */

            // Test's Name
            row[0] = test.getTestName();

            // Test's Version
            String pass = String.valueOf(test.isPassed());
            if (pass.equals("true")) {
                row[versionColumnIndex] = "passed";
            } else {
                row[versionColumnIndex] = "failed";
            }

            // Test's Score
            row[scoreColumnIndex] = String.valueOf(df.format(test.getTestScore()));
        }

        if (tests.size() > lines.size() - 1) {
            for (int i = lines.size() - 1; i < tests.size(); i++) {
                Test test = tests.get(i);
                String[] newRow = new String[lines.get(0).length];

                newRow[0] = test.getTestName();
                String pass = String.valueOf(test.isPassed());
                newRow[versionColumnIndex] = pass.equals("true") ? "passed" : "failed";
                newRow[scoreColumnIndex] = String.valueOf(df.format(test.getTestScore()));

                lines.add(newRow);
            }
        }

        // Writing back to the same file
        CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));
        csvWriter.writeAll(lines);
        csvWriter.close();
        System.out.println("Result.csv file updated successfully! -> " + filePath);
    }

    private static int getVersionColumnIndex(String[] header, String applicationVersion) {
        for (int i = 0; i < header.length; i++) {
            if (applicationVersion.equals(header[i])) {
                return i;
            }
        }
        header[header.length - 1] = applicationVersion;
        return header.length - 1;
    }

    private int getScoreColumnIndex(String[] header) throws IOException, CsvValidationException {
        for (int i = 0; i < header.length; i++) {
            if ("Score".equals(header[i])) return i;
        }
        throw new IllegalArgumentException("Could not find 'Score' column inside CSV file.");
    }
}