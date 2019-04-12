package ly.betime.shuriken.preferences;

public class Preference {
    private final String name;
    private final int labelStringId;
    private final Class type;
    private final Object defaultValue;

    public Preference(String name, Object defaultValue, int labelStringId, Class type) {
        this.name = name;
        this.labelStringId = labelStringId;
        this.type = type;
        this.defaultValue = defaultValue;
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

    public Object getDefaultValue() {
        return defaultValue;
    }
}
