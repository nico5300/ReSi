package me.ocin5300.ReSi;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

public class Datei {
    private String content;
    private Path file;

    public Datei(String path) throws IOException{
        file = Paths.get(path);

        if (Files.exists(file)) {
            byte[] list = Files.readAllBytes(file);

            content = new String(list, Charset.forName("UTF-8"));
        } else {
            content = "";
        }
    }

    public Datei() {
        content = "";
        file = null;
    }

    public File getFile() {
        return file.toFile();
    }

    public String getContent() {
        return content;
    }

    public void saveNewContent(String newContent) throws IOException {

        Files.write(file, newContent.getBytes(Charset.forName("UTF-8")));
        content = newContent;
    }

    public boolean isDifferent(String diffContent) {
        return !content.equals(diffContent);
    }

    public boolean fileAlreadyExisting() {
        return Files.exists(file);
    }

    public boolean isInitial() {
        return file == null;
    }
}
