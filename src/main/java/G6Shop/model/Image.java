package G6Shop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Image {
    @Id
    @GeneratedValue
    Long id;

    @Lob
    byte[] content;

    String name;

    public Image() {

    }

    public Image(byte[] bytes, String name) {
        this.content = bytes;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
