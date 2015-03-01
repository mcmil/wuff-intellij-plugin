package pl.cmil.wuff.plugin.diagnostic;

import java.util.Comparator;

public class BundleDiagnosis {
    private String name;
    private Status status;
    private int id;

    public BundleDiagnosis(String name, Status status, int id) {
        this.name = name;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId() + " " + getName() + " " + getStatus();
    }

    public enum Status {
        OK, NOT;

        public static final String INSTALLED = "INSTALLED";

        public static Status getFrom(String status) {
            if (status.equalsIgnoreCase(INSTALLED)) {
                return NOT;
            }
            return OK;
        }

    }

    public class Sorter implements Comparator<BundleDiagnosis> {
        @Override
        public int compare(BundleDiagnosis x, BundleDiagnosis y) {
            int statusCompare = x.getStatus().compareTo(y.getStatus());
            if (statusCompare != 0) {
                return statusCompare;
            }
            return Integer.compare(x.getId(), y.getId());
        }
    }
}
