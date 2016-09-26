package metrics;

import model.PaprikaClass;

/**
 * Created by Geoffrey Hecht on 20/05/14.
 */
public class NumberOfAttributes extends UnaryMetric<Integer> {

    private NumberOfAttributes(PaprikaClass paprikaClass, int value) {
        this.value = value;
        this.entity = paprikaClass;
        this.name = "number_of_attributes";
    }

    public static NumberOfAttributes createNumberOfAttributes(PaprikaClass paprikaClass, int value) {
        NumberOfAttributes numberOfAttributes = new NumberOfAttributes(paprikaClass, value);
        numberOfAttributes.updateEntity();
        return numberOfAttributes;
    }

}
