package G6Shop.repositorymanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import G6Shop.model.Contact;
import G6Shop.model.Version.VersionName;

import java.util.*;

interface ContactRepository extends CrudRepository<Contact, Integer> {

    @Query("SELECT c FROM Contact c where c.name LIKE :name")
    List<Contact> findContactByName(String name);

    @Query("SELECT c FROM Contact c where c.size LIKE :size")
    List<Contact> findContactBySize(String size);

}

@Component
public class ContactRepositoryManager extends AbstractRepositoryManager {

    public List<Contact> findContactByName(String name) {
        return contactRepository.findContactByName(name);
    }
    
    public List<Contact> findContactBySize(String size) {
    return contactRepository.findContactBySize(size);
    }

    @Autowired
    private ContactRepository contactRepository;

    public Iterable<Contact> findAll() {
        return contactRepository.findAll();
    }

    public Optional<Contact> findById(int id) {
        return contactRepository.findById(id);
    }

    public void save(Contact contact) {
        contactRepository.save(contact);
        super.updateVersion(VersionName.CONTACT);
    }

    public void deleteById(int id) {
        Optional<Contact> optional = contactRepository.findById(id);
        if (optional.isPresent()) {
            Contact c = optional.get();
            fileLocationService.deleteImage(c.getDrawablePath());
            contactRepository.deleteById(c.getId());
        }
        updateVersion(VersionName.CONTACT);
    }

    public long getVersion() {
        return super.getVersion(VersionName.CONTACT);
    }

    public long count() {
        return contactRepository.count();
    }

}
