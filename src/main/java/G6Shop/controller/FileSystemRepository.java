package G6Shop.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

@Repository
class FileSystemRepository {

    public static final String IMAGES_DIR = getResourceDir();

    Logger logger = LoggerFactory.getLogger(FileSystemRepository.class);

    public void save(byte[] content, String imagePathString) {
        try {
            var path = Paths.get(imagePathString);
            Files.createDirectories(path.getParent());
            Files.write(path, content);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static String getResourceDir() {
        String path = System.getProperty("user.dir");
        String os = System.getProperty("os.name");
        if (os.contains("Windows") && path.startsWith("/")) {
            path = path.substring(1);
        }
        path += "/src/main/webapp/WEB-INF/images/repo/";
        return path;
    }

    public FileSystemResource findInFileSystem(String location) {
        return new FileSystemResource(Paths.get(location));
    }

    public void delete(String imageName) {
        if (imageName == null) {
            return;
        }
        var p = Paths.get(imageName);
        if (Files.exists(p)) {
            try {
                Files.delete(p);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}