package G6Shop.repositorymanager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import G6Shop.controller.FileLocationService;
import G6Shop.model.Version;
import G6Shop.repository.VersionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public abstract class AbstractRepositoryManager {

    Logger logger = LoggerFactory.getLogger(AbstractRepositoryManager.class);

    @Autowired
    private VersionRepository versionsRepository;

    @Autowired
    FileLocationService fileLocationService;

    protected void updateVersion(Version.VersionName versionName) {
        logger.info("executando updateVersion");
        List<Version> versions = versionsRepository.findByName(versionName.toString());
        if (!versions.isEmpty()) {
            var v = versions.get(0);
            long currentVersion = v.getNumber();
            if (currentVersion == Long.MAX_VALUE)
                currentVersion = 1L;
            else
                ++currentVersion;
            v.setNumber(currentVersion);
            versionsRepository.save(v);
        }
    }

    public long getVersion(Version.VersionName versionName) {
        List<Version> versions = versionsRepository.findByName(versionName.toString());
        if (!versions.isEmpty()) {
            return versions.get(0).getNumber();
        }
        return -1L;
    }
}
