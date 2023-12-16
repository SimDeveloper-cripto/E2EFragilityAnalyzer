import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import com.opencsv.CSVWriter;
import java.text.DecimalFormat;

public class WriterCSV {
    private final List<Test> tests;
    private final String filePath;
    private final DecimalFormat df;

    public WriterCSV(List<Test> tests, String filePath, DecimalFormat df) {
        this.tests = tests;
        this.filePath = filePath;
        this.df = df;
    }

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
}