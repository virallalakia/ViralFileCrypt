# ViralFileCrypt
A very fast, simple and easy to use file encryption/decryption tool.

## Features
* **`HIGHLY SECURE`** - All the encrypted files generated using this tool are highly secure.
* **`LIGHTING FAST`** - Encrypt/Decrypt many files within few seconds.
* **`MULTI-FILE SUPPORT`** - Wildcard supported, encrypt/decrypt many files within diffrent folders in one execution.
* **`KEY IS NEVER SAVED`** - The `salt` is only with you, no one can decrypt your files without that exact salt.
* **`EXACT SAME SIZE`** - All encrypted/decrypted files are generated with the exact same size of the original files.

## How to use ViralFileCrypt tool
* Download ViralFileCrypt.jar (see [downloads](downloads)).
* Use Java 1.8 (see [dependencies](#dependencies)) to run the jar file with supported [options](#options).

## Options
* Syntax:
    `java -jar ViralFileCrypt.jar [-f] [-s <salt-string> -i <input-file(s)> [-o <output-file/directory>]]`
* Options:
    ```
       <without-any-options>   prints this help

        -f                     logs will be turned off,
        or     <verbose-off>   nothing will be be logged on the console,
      --off                    including errors and this help

        -s                     salt to be used to encrpyt/decrypt the file(s), same salt
        or     <salt-string>   needs to be provided to decrypt the encrypted file(s),
      --salt                   salt must be any string with the length of multiple of
                               4 (at least 4 characters)

        -i                     input file(s) to be encrpyted/decrypted, path can be
        or    <input-file(s)>  absolute/relative to current directory, use wildcards
      --input                  (*) to provide multiple filenames and (**) to include
                               all sub-directories too, e.g.:
                               abc/x.txt    - x.txt file in abc directory
                               abc/*        - all files in abc directory
                               abc/*.*      - all files in abc directory with any extension
                               abc/*.txt    - all files in abc directory with .txt extension
                                              and with any name
                               abc/xyz.*    - all files in abc directory with any extension
                                              and with xyz name
                               abc/**/*     - all files in abc and its sub-directories
                               abc/**/*.*   - all files in abc directory and its sub-
                                              directories with any extension
                               abc/**/*.txt - all files in abc directory and its sub-
                                              directories with .txt extension and with any name
                               abc/**/xyz.* - all files in abc directory and its sub-
                                              directories with any extension and with xyz name

        -o                     optional option
        or   <output-file/dir> if provided, output file/directory for generated
     --output                    encrpyted/decrypted files, it will be used as output file if
                                 input is a single file, it will be used as output directory
                                 and all files will be generated using input file/directory
                                 names and structure if input is multiple files,
                               if not provided, [VFC] will be used as output directory under
                                 input directory and all files will be generated using input
                                 file/directory names and structure
    ```

## Downloads
* Download ViralFileCrypt.jar from [here](https://cdn.rawgit.com/virallalakia/ViralFileCrypt/master/dist/ViralFileCrypt.jar).

## Dependencies
* Java 1.8 (jdk 8) and above to compile the source.
* Java 1.8 (jdk 8 or jre 8) and above to run the jar file.
