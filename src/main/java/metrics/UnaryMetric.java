package metrics;

import model.*;

/**
 * Created by Geoffrey Hecht on 20/05/14.
 */
public abstract class UnaryMetric<E> extends Metric{
    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }

    protected void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String toString() {
        return this.entity + " " + this.name + " : "+ this.value;
    }

    protected void updateEntity(){
        entity.addMetric(this);
    }
}
