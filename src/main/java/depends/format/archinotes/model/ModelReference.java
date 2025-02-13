package depends.format.archinotes.model;

public class ModelReference {
    private String type;
    private String mainId;
    private String internalId;

    public ModelReference(String type, String mainId, String internalId) {
        this.type = type;
        this.mainId = mainId;
        this.internalId = internalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }
} 