package ly.betime.shuriken.preferences;

public class Preference {
    private final String name;
    private final int labelStringId;
    private final Class type;

    public Preference(String name, int labelStringId, Class type) {
        this.name = name;
        this.labelStringId = labelStringId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getLabelStringId() {
        return labelStringId;
    }

    public Class getType() {
        return type;
    }

}
