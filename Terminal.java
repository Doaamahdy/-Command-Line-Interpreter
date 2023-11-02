package com.mycompany.terminal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

class Parser {

    String commandName;
    String[] args;
    String text;

    // Constructor
    public Parser() {
        this.commandName = null;
        this.args = null;
    }

    public boolean parse(String input) {

        if (input.contains(">")) {
            String[] parts = input.trim().split(">", 2);
            this.commandName = parts[0];
            this.args = new String[] { parts[1].trim() };
            return true;
        }
        // Split the input into commandName and args based on whitespace
        String[] parts = input.trim().split("\\s+", 2);

        if (parts.length >= 1) {
            this.commandName = parts[0];
        } else {
            // If no command is provided, return false to indicate parsing failure
            return false;
        }

        if (parts.length == 2) {
            this.text = parts[1];
            this.args = parts[1].split("\\s+");
        } else {
            this.args = new String[0];
        }

        // Parsing succeeded
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public String getText() {
        return text;
    }
}

public class Terminal {

    Parser parser;

    Terminal() {
        this.parser = new Parser();
    }

    public static void main(String[] args) {
        System.out.println("Hello in our CLI");
        Terminal t = new Terminal();
        t.chooseCommandAction();
    }

    public void chooseCommandAction() {

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print('>');
            String input = scanner.nextLine();
            parser.parse(input);

            if (input.contains(">")) {
                executeCommandWithOutputRedirect(parser.commandName, ">", parser.getArgs()[0]);
                continue;
            }

            switch (parser.getCommandName()) {

                case "echo":
                    echo(parser.getText());
                    break;
                case "pwd":
                    pwd();
                    break;
                case "rmdir":
                    if ("*".equals(parser.getText())) {
                        rmdir(new File(System.getProperty("user.dir")));
                    } else {
                        rmdir(parser.getArgs()[0]);
                    }
                    break;
                case "mkdir":
                    if (parser.getArgs().length == 1) {
                        mkdir(parser.getArgs()[0], System.getProperty(input));
                    } else {
                        for (int i = 0; i < parser.getArgs().length - 1; i++) {
                            mkdir(parser.getArgs()[i], parser.getArgs()[parser.getArgs().length - 1]);

                        }
                    }
                    break;
                case "cat":
                    if (parser.getArgs().length == 1) {
                        cat(parser.getArgs()[0]);
                    } else if (parser.getArgs().length == 2) {
                        cat(parser.getArgs()[0], parser.getArgs()[1]);
                    }
                    break;
                case "cd":
                    if (parser.getArgs().length == 0) {
                        cdHome();
                    } else if (parser.getArgs().length == 1) {
                        if ("..".equals(parser.getArgs()[0])) {
                            cd();
                        } else {
                            cd(parser.getArgs()[0]);
                        }
                    }
                    case "cp":
                    if (parser.getArgs().length == 2) {
                        cp(parser.getArgs()[0], parser.getArgs()[1]);
                    } else {
                        System.out.println("Invalid number of arguments.Usage: cp sourcefiledpath destinationfilepath. ex : cp java/am.tx jave/pm.txt ");
                    }
                    break;
                    case "Ls":
                    listCurrentDirectory();
                    break;
                case "Ls-r":
                    listCurrentDirectoryReversed();
                    break;
                case "exit":
                    return;

            }
        }

    }

    public void pwd() {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current directory: " + currentDirectory);

    }

    public void mkdir(String directoryName, String directoryPath) {
        File directory = new File(directoryPath, directoryName);

        if (directory.exists()) {
            System.out.println("Directory :" + directoryName + " already exists ");
        } else {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Directory :" + directoryName + "created successfully ");
            } else {
                System.out.println("Failed to create directory:" + directoryName);
            }
        }
    }

    public void echo(String message) {
        System.out.println(message);
    }
    // Case 1 : reome emprty dires inside specific dire

    public void rmdir(File directory) {
        if (directory.isDirectory()) {
            File[] subFiles = directory.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    rmdir(subFile);
                }
            }
            if (directory.listFiles() == null || directory.listFiles().length == 0) {
                boolean removed = directory.delete();
                if (removed) {
                    System.out.println("Directory '" + directory.getAbsolutePath() + "' removed.");
                }
            }
        }
    }

    // Case 2: remove dire only if its empty
    public void rmdir(String path) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            boolean removed = directory.delete();
            if (removed) {
                System.out.println("Directory '" + path + "' removed.");
            } else {
                System.out.println("Failed to remove directory '" + path + "'.");
            }
        } else {
            System.out.println("Directory does not exist: " + path);
        }
    }
    // Case 1: Print the content of a single file

    public void cat(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Case 2: Concatenate and print the content of two files
    public void cat(String fileName1, String fileName2) {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(fileName1));
                BufferedReader reader2 = new BufferedReader(new FileReader(fileName2))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = reader2.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Case 1: Change to the home directory
    public void cdHome() {
        String homeDirectory = System.getProperty("user.home");
        System.setProperty("user.dir", homeDirectory);
        System.out.println("Current directory changed to the home directory: " + homeDirectory);
    }

    // Case 2: Change to the previous directory
    public void cd() {
        String currentDirectory = System.getProperty("user.dir");
        File currentDir = new File(currentDirectory);
        String parentDir = currentDir.getParent();
        if (parentDir != null) {
            System.setProperty("user.dir", parentDir);
            System.out.println("Current directory changed to the previous directory: " + parentDir);
        } else {
            System.out.println("No parent directory exists.");
        }
    }

    // Case 3: Change to the specified directory
    public void cd(String path) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            System.setProperty("user.dir", directory.getAbsolutePath());
            System.out.println("Current directory changed to: " + directory.getAbsolutePath());
        } else {
            System.out.println("Directory does not exist: " + path);
        }
    }

    public void executeCommandWithOutputRedirect(String command, String outputRedirect, String fileName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(new File(fileName)));

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Command executed successfully. Output redirected to " + fileName);
            } else {
                System.out.println("Command execution failed.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void cp(String sourcePath, String destinationPath) {
       File sourceFile = new File(sourcePath);
       File destinationFile = new File(destinationPath);

       try {
           Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
           System.out.println("File copied successfully.");
       } catch (IOException e) {
           e.printStackTrace();
           System.err.println("An error occurred while copying the file.");
       }
   }
    public static void listCurrentDirectory() {
        try {
            String currentDirPath = System.getProperty("user.dir");
            File currentDir = new File(currentDirPath);

            if (!currentDir.isDirectory()) {
                System.out.println("Current path is not a directory.");
                return;
            }

            File[] files = currentDir.listFiles();

            if (files == null) {
                System.out.println("Error listing the current directory.");
                return;
            }

            Arrays.sort(files);

            for (File file : files) {
                System.out.println(file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error listing the current directory.");
        }
    }
    public static void listCurrentDirectoryReversed() {
        try {
            String currentDirPath = System.getProperty("user.dir");
            File currentDir = new File(currentDirPath);

            if (!currentDir.isDirectory()) {
                System.out.println("Current path is not a directory.");
                return;
            }

            File[] files = currentDir.listFiles();

            if (files == null) {
                System.out.println("Error listing the current directory.");
                return;
            }

            Arrays.sort(files, Collections.reverseOrder());

            for (File file : files) {
                System.out.println(file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error listing the current directory.");
        }
    }

}
