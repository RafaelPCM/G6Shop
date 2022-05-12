package G6Shop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.lang.NonNull;

@Entity
public class Products implements ModelWithDrawablePath {

    @Id
    @GeneratedValue
    @NonNull
    private int id;
    @NonNull
    private String name = "";
    @NonNull
    private String size = "";
    @NonNull
    private String drawablePath;

    private Integer price;

    private Integer stock;

    private Integer buystock;

    
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

    public String getDrawablePath() {
        return this.drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }

    public Integer getPrice() {
        return this.price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }


    public Integer getStock() {
        return this.stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Integer getBuystock() {
        return this.buystock;
    }

    public void setBuystock(Integer buystock) {
        this.buystock = buystock;
    }

}
