package G6Shop.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;

@Entity
public class Products implements ModelWithDrawablePath {

    @Id
    @GeneratedValue
    @NonNull
    private int id;
    @NonNull
    private String name = "";

    private String size;

    private int price;

    private String drawablePath;

    @NonNull
    private long version;

    
    public Products() {
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDrawablePath() {
        return this.drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    

    // public TrainLine getTrainLine() {
    //     return this.trainLine;
    // }

    // public void setTrainLine(TrainLine trainLine) {
    //     this.trainLine = trainLine;
    // }

    // @Override
    // public boolean equals(Object o) {
    //     if (o == this)
    //         return true;
    //     if (!(o instanceof Products)) {
    //         return false;
    //     }
    //     Products station = (Products) o;
    //     return id == station.id && Objects.equals(name, station.name)
    //             && Objects.equals(drawablePath, station.drawablePath) && version == station.version
    //             && stationOrder == station.stationOrder && Objects.equals(trainLine, station.trainLine);
    // }
    // 
    // @Override
    // public String toString() {
    //     return "{" + " id='" + getId() + "'" + ", name='" + getName() + "'" + ", drawablePath='" + getDrawablePath()
    //             + "'" + ", version='" + getVersion() + "'" + ", stationOrder='" + getStationOrder() + "'"
    //             + ", trainLine='" + getTrainLine() + "'" + "}";
    // }

}