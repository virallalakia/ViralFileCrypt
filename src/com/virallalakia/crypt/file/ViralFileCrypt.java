package com.virallalakia.crypt.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Viral Lalakia (virallalakia)
 * @copyright 2017 Viral Lalakia
 * @license MIT License (https://opensource.org/licenses/MIT)
 * @version 1.0
 */
public class ViralFileCrypt {

    private static final String FILE_SEP = File.separator;
    private static final int BYTE_BUFFER_SIZE = 64 * 1024; // 64kb
    private static final String DEFAULT_OUTPUT_DIR = "[VFC]";

    private static boolean verbose = true;
    private static byte[] saltBytes = "_##_V_I_R_A_L__L_A_L_A_K_I_A_##_".getBytes();
    private static int[] salt = new int[saltBytes.length / 4];
    private static String input = "";
    private static String output = "";

    private static List<String> errors = new ArrayList<String>();

    static class ViralFileListMaker implements FileVisitor<Path> {

        private boolean allFiles;
        private String name;
        private String ext;
        private String exact;

        private Set<String> paths;

        public ViralFileListMaker(boolean allFiles, String name, String ext, String exact) {
            super();
            this.allFiles = allFiles;
            this.name = name;
            this.ext = ext;
            this.exact = exact;
            this.paths = new HashSet<String>();
        }

        public boolean isAllFiles() {
            return allFiles;
        }

        public String getName() {
            return name;
        }

        public String getExt() {
            return ext;
        }

        public String getExact() {
            return exact;
        }

        public Set<String> getPaths() {
            return new HashSet<String>(paths);
        }

        public void resetPaths() {
            paths.clear();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) {
            File file = path.toFile();
            String fileName = file.getName();
            if (file.isFile()) {
                if (allFiles || (exact != null && exact.equals(fileName))
                        || (exact == null && name == null && ext == null && fileName.contains("."))
                        || (exact == null && name != null && ext == null && fileName.startsWith(name + "."))
                        || (exact == null && name == null && ext != null && fileName.endsWith("." + ext))) {
                    paths.add(file.getAbsolutePath());
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ViralFileListMaker [allFiles=").append(allFiles).append(", name=").append(name)
                    .append(", ext=").append(ext).append(", exact=").append(exact).append("]");
            return builder.toString();
        }

    }

    private static void printErrors() {
        if (!verbose || errors == null || errors.size() == 0) {
            return;
        }
        System.out.println("Error(s) while running ViralFileCrypt for provided options:");
        for (String error : errors) {
            System.out.println("- " + error);
        }
        System.out.println("");
        System.out.println("Tip: To get help, run command without any options");
        System.out.println("");
    }

    private static void printHelp() {
        if (!verbose) {
            return;
        }
        System.out.println("---------------------------------------");
        System.out.println("  Usage and Syntax for ViralFileCrypt  ");
        System.out.println("---------------------------------------");
        System.out.println("");
        System.out.println("Syntax: java -jar ViralFileCrypt.jar"
                + " [-f] [-s <salt-string> -i <input-file(s)> [-o <output-file/directory>]]");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("");
        System.out.println("   <without-any-options>   prints this help");
        System.out.println("");
        System.out.println("    -f                     logs will be turned off,");
        System.out.println("    or     <verbose-off>   nothing will be be logged on the console,");
        System.out.println("  --off                    including errors and this help");
        System.out.println("");
        System.out.println("    -s                     salt to be used to encrpyt/decrypt the file(s), same salt");
        System.out.println("    or     <salt-string>   needs to be provided to decrypt the encrypted file(s),");
        System.out.println("  --salt                   salt must be any string with the length of multiple of");
        System.out.println("                           4 (at least 4 characters)");
        System.out.println("");
        System.out.println("    -i                     input file(s) to be encrpyted/decrypted, path can be");
        System.out.println("    or    <input-file(s)>  absolute/relative to current directory, use wildcards");
        System.out.println("  --input                  (*) to provide multiple filenames and (**) to include");
        System.out.println("                           all sub-directories too, e.g.:");
        System.out.println("                           abc/x.txt - x.txt file in abc directory");
        System.out.println("                           abc/*     - all files in abc directory");
        System.out.println("                           abc/*.*   - all files in abc directory with any extension");
        System.out.println("                           abc/*.txt - all files in abc directory with .txt extension");
        System.out.println("                                       and with any name");
        System.out.println("                           abc/xyz.* - all files in abc directory with any extension");
        System.out.println("                                       and with xyz name");
        System.out.println("                           abc/**/*  - all files in abc and its sub-directories");
        System.out.println("");
        System.out.println("    -o                     optional option");
        System.out.println("    or   <output-file/dir> if provided, output file/directory for generated");
        System.out.println(" --output                    encrpyted/decrypted files, it will be used as output file if");
        System.out.println("                             input is a single file, it will be used as output directory");
        System.out.println("                             and all files will be generated using input file/directory");
        System.out.println("                             names and structure if input is multiple files,");
        System.out.println("                           if not provided, [VFC] will be used as output directory under");
        System.out.println("                             input directory and all files will be generated using input");
        System.out.println("                             file/directory names and structure");
        System.out.println("");
        System.out.println("");
    }

    private static void prepareSalt(String saltStr) {
        if (saltStr != null && saltStr.length() >= 4) {
            saltBytes = saltStr.getBytes();
            salt = new int[saltBytes.length / 4];
        } else {
            errors.add("salt must be any string with the length of multiple of 4 (at least 4 characters)");
            return;
        }
        for (int i = 0; i < salt.length; i++) {
            salt[i] = (((int) saltBytes[4 * i]) << 24) + (((int) saltBytes[4 * i + 1]) << 16)
                    + (((int) saltBytes[4 * i + 2]) << 8) + (((int) saltBytes[4 * i + 3]));
        }
    }

    private static boolean cryptFileByByte(String ipFilePath, String opFilePath) {
        File ipFile = new File(ipFilePath);
        File opFile = new File(opFilePath);
        if (!ipFile.exists() || !ipFile.isFile()) {
            errors.add("input file does not exists: " + ipFilePath);
            return false;
        }
        if (opFile.exists()) {
            try {
                opFile.delete();
            } catch (SecurityException e) {
                errors.add("could not delete existing output file: " + opFilePath + " [" + e.getMessage() + "]");
                return false;
            }
        }
        try {
            opFile.getParentFile().mkdirs();
            if (!opFile.createNewFile()) {
                errors.add("could not create output file: " + opFilePath);
                return false;
            }
        } catch (SecurityException | IOException e) {
            errors.add("could not create output file: " + opFilePath + " [" + e.getMessage() + "]");
            return false;
        }
        try (FileInputStream ipFis = new FileInputStream(ipFile);
                FileOutputStream opFis = new FileOutputStream(opFile);
                FileChannel ipChannel = ipFis.getChannel();
                FileChannel opChannel = opFis.getChannel()) {
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(BYTE_BUFFER_SIZE);
            byteBuf.clear();
            long byteLimit = 0;
            long intLimit = 0;
            int[] intArr = new int[BYTE_BUFFER_SIZE / 4];
            while ((byteLimit = ipChannel.read(byteBuf)) != -1) {
                while (byteBuf.position() % 4 != 0 && byteBuf.position() + 1 < BYTE_BUFFER_SIZE) {
                    byteBuf.put((byte) 127);
                }
                byteBuf.flip();
                intLimit = (long) Math.ceil(byteBuf.limit() / 4);
                byteBuf.asIntBuffer().get(intArr, 0, (int) intLimit);
                byteBuf.clear();
                for (int i = 0; i < (int) intLimit; i++) {
                    intArr[i] ^= salt[i % salt.length];
                    if (byteBuf.position() + 4 <= byteLimit) {
                        byteBuf.putInt(intArr[i]);
                    } else {
                        if (byteBuf.position() < byteLimit) {
                            byteBuf.put((byte) (intArr[i] >> 24));
                            if (byteBuf.position() < byteLimit) {
                                byteBuf.put((byte) (intArr[i] >> 16));
                                if (byteBuf.position() < byteLimit) {
                                    byteBuf.put((byte) (intArr[i] >> 8));
                                    if (byteBuf.position() < byteLimit) {
                                        byteBuf.put((byte) (intArr[i]));
                                    }
                                }
                            }
                        }
                    }
                }
                byteBuf.flip();
                opChannel.write(byteBuf);
                byteBuf.clear();
            }
        } catch (FileNotFoundException e) {
            errors.add("could not find the file" + " [" + e.getMessage() + "]");
            return false;
        } catch (IOException e) {
            errors.add("error while read/write with file" + " [" + e.getMessage() + "]");
            return false;
        }
        return true;
    }

    private static void processViralFileCrypt() throws IOException {
        Map<String, String> inputOutputFilePaths = new TreeMap<String, String>();
        boolean multiFileFlag = false;
        boolean recursive = false;
        boolean allFiles = false;
        String name = null;
        String ext = null;
        String exact = null;
        String inputDirPath = "";
        String inputFileName = "";
        String outputDirPath = "";

        // process input
        File inputFile = new File(input);
        input = inputFile.getAbsolutePath();

        if (inputFile.exists() && inputFile.isFile()) {
            multiFileFlag = false;
            inputFileName = inputFile.getName();
            inputDirPath = inputFile.getParent();
            inputOutputFilePaths.put(input, "");
        } else if (input.indexOf("*") != -1) {
            multiFileFlag = true;
            inputFileName = inputFile.getName();
            if (inputFileName == null) {
                errors.add("invalid input file(s)");
                return;
            }
            int indexOfInputFileName = input.lastIndexOf(inputFileName);
            if (indexOfInputFileName == -1) {
                errors.add("invalid input file(s)");
                return;
            }
            inputDirPath = input.substring(0, indexOfInputFileName);
            inputFile = new File(inputDirPath);
            recursive = false;
            if (inputFile.exists()) {
                if (!inputFile.isDirectory()) {
                    errors.add("invalid input file(s)");
                    return;
                }
            } else if (!inputDirPath.endsWith(FILE_SEP + "**" + FILE_SEP)) {
                errors.add("invalid input file(s)");
                return;
            } else {
                recursive = true;
                inputDirPath = inputDirPath.substring(0, inputDirPath.length() - 4);
                inputFile = new File(inputDirPath);
                if (!inputFile.exists() || !inputFile.isDirectory()) {
                    errors.add("invalid input file(s)");
                    return;
                }
            }
            if (inputFileName.indexOf("*") != -1) {
                if ("*".equals(inputFileName)) {
                    allFiles = true;
                } else if ("*.*".equals(inputFileName)) {
                    name = null;
                    ext = null;
                } else if (inputFileName.startsWith("*.")) {
                    name = null;
                    ext = inputFileName.substring(2);
                } else if (inputFileName.endsWith(".*")) {
                    name = inputFileName.substring(0, inputFileName.length() - 2);
                    ext = null;
                }
            } else {
                exact = inputFileName;
            }
            ViralFileListMaker viralFileListMaker = new ViralFileListMaker(allFiles, name, ext, exact);
            Files.walkFileTree(inputFile.toPath(), EnumSet.noneOf(FileVisitOption.class),
                    (recursive ? Integer.MAX_VALUE : 1), viralFileListMaker);
            for (String path : viralFileListMaker.getPaths()) {
                inputOutputFilePaths.put(path, "");
            }
        } else {
            errors.add("invalid input file(s)");
            return;
        }

        // process output
        if (output == null || "".equals(output)) {
            outputDirPath = inputDirPath + FILE_SEP + DEFAULT_OUTPUT_DIR;
        } else {
            File outputFile = new File(output);
            output = outputFile.getAbsolutePath();
            if (multiFileFlag) {
                outputDirPath = output;
                if (inputDirPath.equals(outputDirPath)) {
                    outputDirPath = inputDirPath + FILE_SEP + DEFAULT_OUTPUT_DIR;
                }
            } else {
                inputOutputFilePaths.put(input, output);
            }
        }
        if (output == null || "".equals(output) || multiFileFlag) {
            for (String inputPath : inputOutputFilePaths.keySet()) {
                inputOutputFilePaths.put(inputPath, outputDirPath + inputPath.substring(inputDirPath.length()));
            }
        }

        Set<String> inputPathSet = inputOutputFilePaths.keySet();
        int totalInputFiles = inputPathSet.size();
        int count = 0;
        int countSuccess = 0;
        long t = System.currentTimeMillis();
        for (String inputPath : inputPathSet) {
            if (cryptFileByByte(inputPath, inputOutputFilePaths.get(inputPath))) {
                countSuccess++;
            }
            count++;
            if (verbose) {
                if (System.currentTimeMillis() - t >= 4000 && count < totalInputFiles) {
                    t = System.currentTimeMillis();
                    System.out.println(String.format("Processed files: %d of %d (%.2f%%)", count, totalInputFiles,
                            (100.0 * count / totalInputFiles)));
                }
            }
        }
        if (verbose) {
            System.out.println("Process completed");
            System.out.println(String.format("Successful processed files: %d of %d (%.2f%%)", countSuccess,
                    totalInputFiles, (100.0 * countSuccess / totalInputFiles)));
            if (countSuccess != totalInputFiles) {
                System.out.println(
                        String.format("Errorful processed files: %d of %d (%.2f%%)", (totalInputFiles - countSuccess),
                                totalInputFiles, (100.0 * (totalInputFiles - countSuccess) / totalInputFiles)));
            }
            System.out.println();
        }
    }

    private static void processOptions(String[] options) throws IOException {
        boolean flagHelp = true;
        boolean flagSalt = false;
        boolean flagInput = false;
        boolean flagOutput = false;
        for (int i = 0; i < options.length; i++) {
            switch (options[i]) {
                case "-f":
                case "--off":
                    flagHelp = false;
                    verbose = false;
                    break;
                case "-s":
                case "--salt":
                    flagHelp = false;
                    if (flagSalt) {
                        errors.add("provide option -s or --salt only once");
                        break;
                    }
                    if (i + 1 >= options.length || options[i + 1] == null || options[i + 1].length() % 4 != 0) {
                        errors.add("provide salt with the length of multiple of 4 (at least 4 characters)");
                        break;
                    }
                    prepareSalt(options[i + 1]);
                    flagSalt = true;
                    i++;
                    break;
                case "-i":
                case "--input":
                    flagHelp = false;
                    if (flagInput) {
                        errors.add("provide option -i or --input only once");
                        break;
                    }
                    if (i + 1 >= options.length || options[i + 1] == null || "".equals(options[i + 1])) {
                        errors.add("provide valid input");
                        break;
                    }
                    input = options[i + 1];
                    flagInput = true;
                    i++;
                    break;
                case "-o":
                case "--output":
                    flagHelp = false;
                    if (flagOutput) {
                        errors.add("provide option -o or --output only once");
                        break;
                    }
                    flagOutput = true;
                    if (i + 1 >= options.length || options[i + 1] == null || "".equals(options[i + 1])) {
                        errors.add("provide valid output");
                        break;
                    }
                    output = options[i + 1];
                    i++;
                    break;
            }
        }
        if (flagSalt && flagInput) {
            processViralFileCrypt();
        } else if (flagHelp) {
            printHelp();
        } else {
            if (!flagSalt) {
                errors.add("provide salt with -s or --salt option,"
                        + " salt must be any string with the length of multiple of 4 (at least 4 characters)");
            }
            if (!flagInput) {
                errors.add("provide valid input with -i or --input option");
            }
        }
        if (!flagHelp && errors != null && errors.size() > 0) {
            printErrors();
        }
    }

    public static void main(String[] args) throws IOException {
        processOptions(args);
    }

}
