package depends.format.archinotes.model;

import java.util.UUID;

import depends.deptypes.DependencyType;
import depends.entity.Entity;
import depends.relations.Relation;

public class ModelRelation {
    private String id;
    private String type;
    private String sourceModelId;
    private String targetModelId;
    private String sourceMultiplicity;
    private String targetMultiplicity;
    private String sourceLabel;
    private String targetLabel;
    private String name;
    private DirectionType direction;
    private String callType;
    private boolean isSelf;
    private ModelReference reference;

    public ModelRelation(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceModelId() {
        return sourceModelId;
    }

    public void setSourceModelId(String sourceModelId) {
        this.sourceModelId = sourceModelId;
    }

    public String getTargetModelId() {
        return targetModelId;
    }

    public void setTargetModelId(String targetModelId) {
        this.targetModelId = targetModelId;
    }

    public String getSourceMultiplicity() {
        return sourceMultiplicity;
    }

    public void setSourceMultiplicity(String sourceMultiplicity) {
        this.sourceMultiplicity = sourceMultiplicity;
    }

    public String getTargetMultiplicity() {
        return targetMultiplicity;
    }

    public void setTargetMultiplicity(String targetMultiplicity) {
        this.targetMultiplicity = targetMultiplicity;
    }

    public String getSourceLabel() {
        return sourceLabel;
    }

    public void setSourceLabel(String sourceLabel) {
        this.sourceLabel = sourceLabel;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DirectionType getDirection() {
        return direction;
    }

    public void setDirection(DirectionType direction) {
        this.direction = direction;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public boolean getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public ModelReference getReference() {
        return reference;
    }

    public void setReference(ModelReference reference) {
        this.reference = reference;
    }


    public static ModelRelation build(Integer entity, Integer item, Relation relation) {
        ModelRelation modelRelation = new ModelRelation();
        modelRelation.setId(UUID.randomUUID().toString());
        modelRelation.setType(relation.getType());
        modelRelation.setSourceModelId(String.valueOf(entity));
        modelRelation.setTargetModelId(String.valueOf(item));
        modelRelation.setName(relation.getType());
        return modelRelation;
    }
}