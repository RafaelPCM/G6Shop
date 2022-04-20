package G6Shop.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "local_date", columnDefinition = "DATE")
    private LocalDate localDate = LocalDate.now();

    private String description = "";

    public int getId() {
        return this.id;
    }

    public LocalDate getLocalDate() {
        return this.localDate;
    }

    public String getLocalDateTimeInString() {
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
