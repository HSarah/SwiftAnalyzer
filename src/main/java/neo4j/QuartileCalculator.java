package neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Geoffrey Hecht on 14/08/15.
 */
public class QuartileCalculator {
    protected QueryEngine queryEngine;
    protected GraphDatabaseService graphDatabaseService;

    public QuartileCalculator(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
        graphDatabaseService = queryEngine.getGraphDatabaseService();
    }


    public void calculateClassComplexityQuartile() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE NOT HAS(n.is_interface) AND n.class_complexity<>0  RETURN percentileCont(n.class_complexity,0.25) as Q1, percentileCont(n.class_complexity,0.5) as MED, percentileCont(n.class_complexity,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_CLASS_COMPLEXITY.csv");
    }

    public void calculateCyclomaticComplexityQuartile() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Method) WHERE NOT HAS(n.is_getter) AND NOT HAS(n.is_setter) AND (n.cyclomatic_complexity > 0 AND n.number_of_lines>0 ) RETURN percentileCont(n.cyclomatic_complexity,0.25) as Q1, percentileCont(n.cyclomatic_complexity,0.5) as MED, percentileCont(n.cyclomatic_complexity,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_CYCLOMATIC_COMPLEXITY.csv");
    }

    public void calculateNumberofMethodLines() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Method) WHERE NOT HAS(n.is_getter) AND NOT HAS(n.is_setter) AND n.number_of_lines > 0 RETURN percentileCont(n.number_of_lines,0.25) as Q1, percentileCont(n.number_of_lines,0.5) as MED, percentileCont(n.number_of_lines,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_METHOD_LINES.csv");
    }

    public void calculateNumberofClassLines() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE NOT HAS(n.is_interface)  AND (n.number_of_lines >10 AND n.number_of_methods<>0) RETURN percentileCont(n.number_of_lines,0.25) as Q1, percentileCont(n.number_of_lines,0.5) as MED, percentileCont(n.number_of_lines,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_CLASS_LINES.csv");
    }

    public void calculateNumberofViewControllerLines() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE NOT HAS(n.is_interface)  AND (n.number_of_lines >0 AND n.number_of_methods<>0  AND n.is_view_controller) RETURN percentileCont(n.number_of_lines,0.25) as Q1, percentileCont(n.number_of_lines,0.5) as MED, percentileCont(n.number_of_lines,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_VC_LINES.csv");
    }

    public Map calculateQuartile(String nodeType, String property){
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:" + nodeType + ") RETURN percentileCont(n." + property + ",0.25) as Q1,percentileCont(n." + property + ",0.5) as MED, percentileCont(n." + property + ",0.75) as Q3";
            result = graphDatabaseService.execute(query);
            return calculeTresholds(result);
        }
    }

    private Map calculeTresholds(Result result){
        Map<String, Double> res = new HashMap<>();
        //Only one result in that case
        while (result.hasNext())
        {
            Map<String,Object> row = result.next();
            //Sometime neo4J return a double or an int... With toString it's works in all cases
            double q1 = Double.valueOf(row.get("Q1").toString());
            double med = Double.valueOf(row.get("MED").toString());
            double q3 = Double.valueOf(row.get("Q3").toString());
            double high  = q3 + ( 1.5 * ( q3 - q1));
            double very_high  = q3 + ( 3 * ( q3 - q1));
            res.put("Q1",q1);
            res.put("Q3",q3);
            res.put("MED",med);
            res.put("HIGH (1.5)",high);
            res.put("VERY HIGH (3.0)",very_high);
        }
        return res;
    }

    private Map calculeInversedTresholds(Result result){
        Map<String, Double> res = new HashMap<>();
        //Only one result in that case
        while (result.hasNext())
        {
            Map<String,Object> row = result.next();
            //Sometime neo4J return a double or an int... With toString it's works in all cases
            double q1 = Double.valueOf(row.get("Q1").toString());
            double med = Double.valueOf(row.get("MED").toString());
            double q3 = Double.valueOf(row.get("Q3").toString());
            //double high  = q3 + ( 1.5 * ( q3 - q1));
            double low = q1-(1.5*(q3-q1));
            double very_low=q1-(3*(q3-q1));
            if(very_low<0){
                very_low=0;
            }
            if(low<0){
                low=0;
            }
            //double very_high  = q3 + ( 3 * ( q3 - q1));
            res.put("Q1",q1);
            res.put("Q3",q3);
            res.put("MED",med);
            res.put("LOW (1.5)",low);
            res.put("VERY LOW (3.0)",very_low);
        }
        return res;
    }


    /**
     * Excluding classes implementing 0 or 1 interface
     * @return
     */
    public void calculateNumberOfImplementedInterfacesQuartile() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE n.number_of_implemented_interfaces > 1 RETURN percentileCont(n.number_of_implemented_interfaces,0.25) as Q1, percentileCont(n.number_of_implemented_interfaces,0.5) as MED, percentileCont(n.number_of_implemented_interfaces,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_INTERFACES.csv");
    }

    public void calculateNumberOfMethodsForInterfacesQuartile() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE HAS(n.is_interface) RETURN percentileCont(n.number_of_methods,0.25) as Q1, percentileCont(n.number_of_methods,0.5) as MED, percentileCont(n.number_of_methods,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_METHODS_INTERFACE.csv");
    }

    public void calculateCohesionAmongMethodsOfClass() throws IOException {//CAMC
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE NOT HAS(n.is_interface) AND (n.cohesion_among_methods_of_class<>1) RETURN percentileCont(n.cohesion_among_methods_of_class,0.25) as Q1, percentileCont(n.cohesion_among_methods_of_class,0.5) as MED, percentileCont(n.cohesion_among_methods_of_class,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeInversedTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_CAMC.csv");
    }

    public void calculateNumberOfMethodsQuartile() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE NOT HAS(n.is_interface) RETURN percentileCont(n.number_of_methods,0.25) as Q1, percentileCont(n.number_of_methods,0.5) as MED, percentileCont(n.number_of_methods,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_METHODS.csv");
    }

    public void calculateNumberOfAttributesQuartile() throws IOException {
        Map<String, Double> res;
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (n:Class) WHERE NOT HAS(n.is_interface)  RETURN percentileCont(n.number_of_attributes,0.25) as Q1, percentileCont(n.number_of_attributes,0.5) as MED, percentileCont(n.number_of_attributes,0.75) as Q3";
            result = graphDatabaseService.execute(query);
            res = calculeTresholds(result);
        }
        queryEngine.statsToCSV(res, "_STAT_NB_ATTRIBUTES.csv");
    }
}
