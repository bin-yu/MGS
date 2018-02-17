package com.yyy.server.policy.repo;

import java.io.Serializable;

import com.yyy.server.worker.repo.Worker;
import com.yyy.server.workerIncident.repo.Incident.Category;

public class Condition implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7920024201401820244L;

    private enum PropertyName {
        BLACK_INCIDENTS_CNT {
            @Override
            public int extractProperty(Worker worker) {
                return worker.getIncidentCnts().get(Category.BLACK);
            }
        },
        SCORE {
            @Override
            public int extractProperty(Worker worker) {
                return worker.getScore();
            }
        };
        abstract public int extractProperty(Worker worker);
    }
    private enum Comparator {
        EQUALS {
            @Override
            public boolean eval(int left, int right) {
                return left == right;
            }
        },
        LARGER_THAN {
            @Override
            public boolean eval(int left, int right) {
                return left > right;
            }
        },
        SMALLER_THAN {
            @Override
            public boolean eval(int left, int right) {
                return left < right;
            }
        };
        abstract public boolean eval(int left, int right);
    }

    private PropertyName property;
    private Comparator comparator;
    private int value;

    public Condition() {
        super();
    }

    public Condition(PropertyName property, Comparator comparator, int value) {
        super();
        this.property = property;
        this.comparator = comparator;
        this.value = value;
    }

    public PropertyName getProperty() {
        return property;
    }

    public void setProperty(PropertyName property) {
        this.property = property;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isSatisfiedBy(Worker worker) {
        return this.comparator.eval(this.property.extractProperty(worker), value);
    }
}
