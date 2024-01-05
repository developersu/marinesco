package ru.redrise.marinesco.library;

import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConfigurationProperties(prefix = "marinesco.library")
public class InpxScanner {

    private String filesLocation = "";

    private FileSystemResource inpxFile;

    public void reScan() {
        inpxFile = new FileSystemResource(filesLocation);

        //todo
        Stream.of(inpxFile.getFile().listFiles())
                .forEach(file -> log.info(file.toString()));
    }

    public String getFilesLocation(){
        return filesLocation;
    }

    public void setFilesLocation(String location){
        filesLocation = location;
    }
}
