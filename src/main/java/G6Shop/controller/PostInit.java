package G6Shop.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import G6Shop.model.User;
import G6Shop.model.Version;
import G6Shop.model.Version.VersionName;
import G6Shop.repository.ImageRepository;
import G6Shop.repository.UserRepository;
import G6Shop.repository.VersionRepository;

@Component
class PostInit {

    Logger logger = LoggerFactory.getLogger(PostInit.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    VersionRepository versionsRepository;

    @Autowired
    private FileLocationService fileLocationService;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.standardPassword}")
    private String standardPassword;

    @Value("${admin.standardUserName}")
    private String standardUserName;

    @PostConstruct
    public void init() {
        logger.info("Postinit inicializado");
        Thread t = new Thread(() -> {
            logger.info("Executando cleanNotUsedImagesInServer()");
            cleanNotUsedImagesInServer();
            logger.info("Finalizado cleanNotUsedImagesInServer()");
        });
        t.start();
        logger.info("Executando initializeNonInitializedVersions()");
        initializeNonInitializedVersions();
        logger.info("Executando createAdminUserIfNoUserFound()");
        createAdminUserIfNoUserFound();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        logger.info("Postinit finalizado");
    }


    private void createAdminUserIfNoUserFound() {
        if (userRepository.count() == 0) {
            var adminUser = new User();
            adminUser.setEnabled(true);
            adminUser.setRegistrationNumber("0");
            adminUser.setRole("ADMIN");
            adminUser.setUsername(standardUserName);
            adminUser.setPassword(passwordEncoder.encode(standardPassword));
            userRepository.save(adminUser);
        }
    }

    private void initializeNonInitializedVersions() {
        for (VersionName versionName : Version.VersionName.values()) {
            List<Version> versions = versionsRepository.findByName(versionName.toString());
            if (versions.isEmpty()) {
                var v = new Version();
                v.setName(versionName.toString());
                v.setNumber(1L);
                versionsRepository.save(v);
            }
        }
    }

    private void cleanNotUsedImagesInServer() {
        String dir = FileSystemRepository.IMAGES_DIR;
        final var folder = new File(dir);

        boolean folderExists = folder.exists();

        if (!folderExists)
            folderExists = folder.mkdir();
        if (!folderExists) {
            logger.info("Permissão: não foi possível criar pasta para armazenar imagens do servidor");
            System.exit(1);
        } else {
            Set<String> images = new HashSet<>();

            Iterable<String> iterable = imageRepository.findAllByName();
            Iterator<String> iterator = iterable.iterator();

            while (iterator.hasNext()) {
                String imageName = iterator.next();
                fileLocationService.copyImageToLocalIfNotExists(imageName);
                images.add(imageName);
            }
            for (File fileEntry : folder.listFiles()) {
                String name = fileEntry.getName();
                if (!images.contains(name))
                    try {
                        var pathName = fileEntry.toString();
                        Files.delete(fileEntry.toPath());
                        var logString = String.format("%s foi deletado.", pathName);
                        logger.info(logString);
                    } catch (IOException e) {
                        // Fazer nada
                    }
            }
        }
    }
}
