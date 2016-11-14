package model;

import java.util.ArrayList;

/**
 * Created by Sarra on 12/04/2016.
 */
public class PaprikaFunction extends Entity {
    private PaprikaApp paprikaApp;
    private String returnType;
    private ArrayList<Entity> nestedEntities;

    private PaprikaFunction(String name , String returnType, PaprikaApp paprikaApp) {
        this.name = name;
        this.paprikaApp = paprikaApp;
        this.returnType = returnType;
        nestedEntities=new ArrayList<>();
    }

    public static PaprikaFunction createPaprikaFunction(String name , String returnType, PaprikaApp paprikaApp){
        PaprikaFunction function = new PaprikaFunction(name,returnType,paprikaApp);
        paprikaApp.addPaprikaFunction(function);
        return function;
    }
    public PaprikaApp getPaprikaApp() {
        return paprikaApp;
    }

    public void setPaprikaApp(PaprikaApp paprikaApp) {
        this.paprikaApp = paprikaApp;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ArrayList<Entity> getNestedEntities() {
        return nestedEntities;
    }

    public void addNestedEntity(Entity nestedEntity) {
        this.nestedEntities.add(nestedEntity);
    }
}
