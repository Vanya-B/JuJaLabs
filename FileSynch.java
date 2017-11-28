package ua.com.juja.core.FileSynch;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class FileSynch {

    static int remove, add, update = 0;
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
            synchFiles(sourcePath, destinationPath);
            System.out.println("start synchronization...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("finished : added = " + add + ", removed = " + remove + ", updated = " + update);
    }

    private static void synchFiles(String sourcePath, String destinationPath) throws IOException {
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
                synchFile(sFile, dest);
                synchDirectory(sFile, dest);
            }

        } else {
            throw new IllegalArgumentException("source directory doesn't exist");
        }
    }

    private static void synchDirectory (File sFile, File dest) throws IOException {
        if (sFile.isDirectory()) {
            if (!containsFile(dest, sFile)) {
                File newFile = new File(dest.toPath() + "/" + sFile.getName());
                Files.copy(sFile.toPath(), newFile.toPath());
                add += 1;
                synchFiles(sFile.getPath(), newFile.getPath());
            } else {
                File dFile = getFileFromDirectory(dest, sFile);
                synchFiles(sFile.getPath(), dFile.getPath());
            }
        }
    }

    private static void synchFile (File sFile, File dest) throws IOException {
        if (sFile.isFile()) {
            if (!containsFile(dest, sFile)) {
                File newFile = new File(dest.toPath() + "/" + sFile.getName());
                Files.copy(sFile.toPath(), newFile.toPath());
                add += 1;
            } else {
                File dFile = getFileFromDirectory(dest, sFile);
                if (dFile.length() != sFile.length()) {
                    Files.delete(dFile.toPath());
                    Files.copy(sFile.toPath(), dFile.toPath());
                    update += 1;
                }
            }
        }
    }

    private static void removeFiles(File srcFile, File dest) throws IOException {
        for (File dFile: dest.listFiles()) {
            if (!containsFile(srcFile, dFile)){
                delete(dFile);
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

    private static void delete (File file) throws IOException {
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                remove += 1;
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Files.delete(dir);
                remove += 1;
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
