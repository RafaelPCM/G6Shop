package G6Shop.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

@Entity
public class TdBURL {

    public enum TdBTypes {
        TAX;
    }

    @Id
    @GeneratedValue
    @NonNull
    private int id;

    @Enumerated(EnumType.STRING)
    private TdBTypes name;

    @NonNull
    private String url;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NonNull
    private LocalDateTime localDate;

    @NonNull
    private long version;

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public LocalDateTime getLocalDate() {
        return this.localDate;
    }

    public void setLocalDate(LocalDateTime localDate) {
        this.localDate = localDate;
    }

    public int getId() {
        return this.id;
    }

    public TdBTypes getName() {
        return this.name;
    }

    public void setName(TdBTypes name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
