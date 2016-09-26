package metrics;

import model.PaprikaClass;

/**
 * Created by Geoffrey Hecht on 22/05/14.
 */
public class ClassComplexity extends UnaryMetric<Integer> {

    private ClassComplexity(PaprikaClass paprikaClass) {
        this.value = paprikaClass.getComplexity();
        this.entity = paprikaClass;
        this.name = "class_complexity";
    }

    public static ClassComplexity createClassComplexity(PaprikaClass paprikaClass) {
        ClassComplexity classComplexity =  new ClassComplexity(paprikaClass);
        classComplexity.updateEntity();
        return classComplexity;
    }

}
