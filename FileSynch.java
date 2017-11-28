package ua.com.juja.core.FileSynch;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class FileSynch {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("no parameters : source, destination");
        }

        if (args.length != 2) {
            throw new IllegalArgumentException("wrong number of parameters");
        }

        String sourcePath = args[0];
        String destinationPath = args[1];
        try {
            synchFile(sourcePath, destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void synchFile (String sourcePath, String destinationPath) throws IOException {
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
            removeFiles(src, dest);
            for (File sFile: src.listFiles()) {
                if (sFile.isFile()) {
                    if (!containsFile(dest, sFile)) {
                        File newFile = new File(dest.toPath() + "/" + sFile.getName());
                        Files.copy(sFile.toPath(), newFile.toPath());
                    } else {
                        File dFile = getFileFromDirectory(dest, sFile);
                        if (dFile.length() != sFile.length()) {
                            Files.delete(dFile.toPath());
                            Files.copy(sFile.toPath(), dFile.toPath());
                        }
                    }
                }

                if (sFile.isDirectory()) {
                    if (!containsFile(dest, sFile)) {
                        File newFile = new File(dest.toPath() + "/" + sFile.getName());
                        Files.copy(sFile.toPath(), newFile.toPath());
                    } else {
                        File dFile = getFileFromDirectory(dest, sFile);
                        synchFile(sFile.getPath(), dFile.getPath());
                    }
                }

            }
        } else {
            throw new IllegalArgumentException("source directory doesn't exist");
        }
    }

    private static void removeFiles(File srcFile, File dest) throws IOException {
        for (File dFile: dest.listFiles()) {
            if (!containsFile(srcFile, dFile)){
                Files.delete(dFile.toPath());
            }
        }
    }

    private static boolean containsFile(File directry, File file) {
        for (File dFile : directry.listFiles()) {
            if (dFile.getName().equals(file.getName())) {
                return true;
            }
        }
        return false;
    }

    private static File getFileFromDirectory (File directory, File file) {
        if (directory.isDirectory()) {
            for (File dFile: directory.listFiles()) {
                if (dFile.getName().equals(file.getName())) {
                    return dFile;
                }
            }
        }
        return null;
    }
}
