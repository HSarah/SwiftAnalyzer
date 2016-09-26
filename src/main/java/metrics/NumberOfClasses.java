package metrics;

import model.PaprikaApp;

/**
 * Created by Geoffrey Hecht on 20/05/14.
 */
public class NumberOfClasses extends UnaryMetric<Integer> {

    private NumberOfClasses(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_classes";
    }

    public static NumberOfClasses createNumberOfClasses(PaprikaApp paprikaApp, int value) {
        NumberOfClasses numberOfClasses = new NumberOfClasses(paprikaApp, value);
        numberOfClasses.updateEntity();
        return numberOfClasses;
    }

}
