package G6Shop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.lang.NonNull;

@Entity
public class Version {

    public enum VersionName {
        NOTIFICATIONS("notifications"), STATIONS("stations"), STATION("station"), TRAINMAP("trainmap"),
        TRAINLINE("trainline"), CONTACT("contact"), HOLIDAY("holiday"), TIMETABLE_IMAGE("timetable_image"), PRODUCTS("products");

        String name;

        VersionName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Id
    @GeneratedValue
    private int id;

    @NonNull
    private String name;

    private int elementId;

    public Version() {
    }

    public Version(int id, String name, int elementId, long number) {
        this.id = id;
        this.name = name;
        this.elementId = elementId;
        this.number = number;
    }

    public int getElementId() {
        return this.elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public Version id(int id) {
        setId(id);
        return this;
    }

    public Version name(String name) {
        setName(name);
        return this;
    }

    public Version elementId(int elementId) {
        setElementId(elementId);
        return this;
    }

    public Version number(long number) {
        setNumber(number);
        return this;
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", name='" + getName() + "'" + ", elementId='" + getElementId() + "'"
                + ", number='" + getNumber() + "'" + "}";
    }

    @NonNull
    private long number;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            var otherVersion = (Version) obj;
            return (otherVersion.name.equals(this.name));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumber() {
        return this.number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

}
