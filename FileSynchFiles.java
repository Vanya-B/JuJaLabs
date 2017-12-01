package ua.com.juja.core.FileSynch;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.Files.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileSynchFiles {
    static int remove, add, update = 0;
    public static void main(String[] args) throws IOException {
        checkParameters(args);
        System.out.println("start synchronization...");
        deleteFile(Paths.get(args[0]), Paths.get(args[1]));
        copyFile(Paths.get(args[0]), Paths.get(args[1]));
        System.out.println("finished : added = " + add + ", removed = " + remove + ", updated = " + update);
    }

    private static void checkParameters (String[] parameters) {
        if (parameters == null || parameters.length == 0) {
            throw new IllegalArgumentException("no parameters : source, destination");
        }

        if (parameters.length != 2) {
            throw new IllegalArgumentException("wrong number of parameters");
        }

        if (!Files.isDirectory(Paths.get(parameters[0]))) {
            throw new IllegalArgumentException("source is not directory");
        }

        if (!Files.isDirectory(Paths.get(parameters[1]))) {
            throw new IllegalArgumentException("destination is not directory");
        }
    }

    private static void copyFile (Path src, Path dest) throws IOException {
            walkFileTree(src, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path resDir = dest.resolve(src.relativize(dir));
                if (!exists(resDir)) {
                    copy(dir, resDir);
                    add += 1;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path dFile = dest.resolve(src.relativize(file));
                if (!exists(dFile)) {
                    copy(file, dFile, StandardCopyOption.REPLACE_EXISTING);
                    add += 1;
                } else {
                    if (size(file) != size(dFile)) {
                        copy(file, dFile, StandardCopyOption.REPLACE_EXISTING);
                        update += 1;
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteFile(Path src, Path dest) throws IOException {
        Files.walkFileTree(dest, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) throws IOException {
                Path dFile = src.resolve(dest.relativize(file));
                if (!exists(dFile)) {
                    delete(file);
                    remove += 1;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Path dFile = src.resolve(dest.relativize(dir));
                if (!exists(dFile)) {
                    delete(dir);
                    remove += 1;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
