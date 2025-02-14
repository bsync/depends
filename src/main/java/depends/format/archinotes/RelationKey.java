package depends.format.archinotes;

import java.util.Objects;

public class RelationKey {
    final int fromId;
    final int toId;

    public RelationKey(int fromId, int toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationKey that = (RelationKey) o;
        return fromId == that.fromId && toId == that.toId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, toId);
    }

    @Override
    public String toString() {
        return "RelationKey{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                '}';
    }
} 