package G6Shop.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import G6Shop.model.Image;
import G6Shop.repository.ImageRepository;

@Service
@Component
@ComponentScan("G6Shop.repository")
public class FileLocationService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    FileSystemRepository fileSystemRepository;

    String saveImage(byte[] bytes, String imageNameStation) {
        var instant = Instant.now();
        long timeStampSeconds = instant.getEpochSecond();
        String pathStation;
        if (imageNameStation.contains(".")) {
            String[] splitName = imageNameStation.split("\\.");
            imageNameStation = splitName[0];
            imageNameStation += "_" + timeStampSeconds + "." + splitName[1];
            pathStation = FileSystemRepository.IMAGES_DIR + imageNameStation;
        } else {
            pathStation = FileSystemRepository.IMAGES_DIR + imageNameStation + "_" + timeStampSeconds;
        }
        fileSystemRepository.save(bytes, pathStation);
        imageRepository.save(new Image(bytes, imageNameStation));
        return imageNameStation;
    }

    public void deleteImage(String imageNameStation) {
        List<Image> si = imageRepository.findByName(imageNameStation);
        if (!si.isEmpty()) {
            imageRepository.delete(si.get(0));
        }
        fileSystemRepository.delete(imageNameStation);
    }

    @Nullable
    FileSystemResource findImageResource(String location) {
        FileSystemResource imageResource = fileSystemRepository.findInFileSystem(location);
        if (!imageResource.exists()) {
            List<Image> optionalImageStation = imageRepository.findByName(location);
            if (!optionalImageStation.isEmpty()) {
                var imageStation = optionalImageStation.get(0);
                this.saveImage(imageStation.getContent(), imageStation.getName());
                imageResource = fileSystemRepository.findInFileSystem(location);
            } else {
                return null;
            }
        }
        return imageResource;
    }

    public void copyImageToLocalIfNotExists(String imageName) {
        FileSystemResource imageResourceStation = fileSystemRepository.findInFileSystem(imageName);
        if (!imageResourceStation.exists()) {
            List<Image> optionalImage = imageRepository.findByName(imageName);
            if (!optionalImage.isEmpty()) {
                var image = optionalImage.get(0);
                String pathStation = FileSystemRepository.IMAGES_DIR + image.getName();
                fileSystemRepository.save(image.getContent(), pathStation);
            }
        }
    }
}
