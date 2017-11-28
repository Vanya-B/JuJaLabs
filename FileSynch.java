package ua.com.juja.core.FileSynch;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class FileSynch {

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("no parameters : source, destination");
        }

        if (args.length != 2) {
            throw new IllegalArgumentException("wrong number of parameters");
        }

        String sourcePath = args[0];
        String destinationPath = args[1];
        boolean isSrcExist = Files.exists(Paths.get(sourcePath), NOFOLLOW_LINKS);
        if (isSrcExist) {
            File dest = null;
            Path destPath = Paths.get(destinationPath);
            if (!Files.exists(destPath)) {
                try {
                    dest = new File(String.valueOf(Files.createDirectory(destPath)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                dest = new File(destinationPath);
            }

            File src = new File(sourcePath);
            File [] srcFiles = src.listFiles();
            for (File sFile: srcFiles) {
                if (!containsFile(dest, sFile)) {
                    File newFile = new File(dest.toPath() + "/" + sFile.getName());
                    Files.copy(sFile.toPath(), newFile.toPath());
                }

            }



        } else {
            throw new IllegalArgumentException("source directory doesn't exist");
        }
    }

    private static boolean containsFile(File directry, File file) {
        if (file.isFile()) {
            File [] destFiles = directry.listFiles();
            for (File dFile : destFiles) {
                if (dFile.getName().equals(file.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
