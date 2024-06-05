
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.stat.StatUtils;

/* [DESCRIPTION]
    - This is a very old file. It is not used anymore but could still be useful.
    - The Point Bi-serial Coefficient of Correlation has been calculated using "Scipy (stats module)", a python library.
        - I, personally, didn't work on that (it is a private repository of my professor's assistant) so I will not push the code here.
**/

public class PointBiserialCorrelationCoefficient {
    static List<Boolean> dichotomous = new ArrayList<>(), dichotomousFalse = new ArrayList<>(), dichotomousTrue = new ArrayList<>();
    static List<Double> continuos = new ArrayList<>(), continuosFalse = new ArrayList<>(), continuosTrue = new ArrayList<>();


    private static void setLists(List<Test> testJudged) {
        for (Test test : testJudged) {
            Double score = test.getTestScore();
            Boolean dichotomousValue = test.isLaterGetSuccess();

            continuos.add(score);
            dichotomous.add(dichotomousValue);

            if(dichotomousValue){
                dichotomousTrue.add(true);
                continuosTrue.add(score);
            }
            else{
                dichotomousFalse.add(false);
                continuosFalse.add(score);
            }
        }
    }

    private static double getStandardDeviation(List<Double> continuos) {
        double mean = continuos.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double squaredDiffSum = continuos.stream()
                .mapToDouble(num -> Math.pow(num - mean, 2))
                .sum();

        double variance = squaredDiffSum / continuos.size();
        return Math.sqrt(variance);
    }

    public static double getCorrelation(List<Test> testJudged){
        setLists(testJudged);

        double meanContinuosFalse = StatUtils.mean(continuosFalse.stream().mapToDouble(Double::doubleValue).toArray());
        double meanContinuosTrue  = StatUtils.mean(continuosTrue.stream().mapToDouble(Double::doubleValue).toArray());
        double standardDeviation = getStandardDeviation(continuos);

        int n0 = dichotomousTrue.size();
        int n1 = dichotomousFalse.size();
        int n = dichotomous.size();

        double squarePart = Math.sqrt((n0 * n1) / Math.pow(n,2));
        double numerator = (meanContinuosTrue - meanContinuosFalse) * squarePart;
        return (numerator / standardDeviation);
    }
}