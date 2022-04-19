package G6Shop.repositorymanager;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import G6Shop.model.Holiday;
import G6Shop.model.Version.VersionName;

interface HolidayRepository extends CrudRepository<Holiday, Integer> {
    @Query("SELECT h FROM Holiday h where h.description LIKE :description")
    List<Holiday> findHolidayByDescription(String description);
}

@Component
public class HolidayRepositoryManager extends AbstractRepositoryManager {

    public List<Holiday> findHolidayByDescription(String description) {
        return holidayRepository.findHolidayByDescription(description);
    }

    @Autowired
    private HolidayRepository holidayRepository;

    public Iterable<Holiday> findAll() {
        return holidayRepository.findAll();
    }

    public Optional<Holiday> findById(int id) {
        return holidayRepository.findById(id);
    }

    public void save(Holiday holiday) {
        holidayRepository.save(holiday);
        super.updateVersion(VersionName.HOLIDAY);
    }

    public void deleteById(int id) {
        holidayRepository.deleteById(id);
        super.updateVersion(VersionName.HOLIDAY);
    }

    public long getVersion() {
        return super.getVersion(VersionName.HOLIDAY);
    }

    public long count() {
        return holidayRepository.count();
    }

}
