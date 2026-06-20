# Library Management System Desktop App

A complete beginner-friendly Java desktop project. It opens a window where you can manage books and members for a small library.

## Features

- Desktop window interface using Java Swing
- View all books in a table
- Add new books with a form
- Register members with a form
- View all members in a table
- Issue a book to a member
- Return a book
- Search books by title or author
- Save data locally in the `data` folder

## Requirements

- Java JDK 17 or newer

Check Java:

```sh
java -version
javac -version
```

## Run on Mac or Linux

```sh
cd library-management-system
chmod +x run.sh
./run.sh
```

## Run in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Choose **Open**.
3. Select the `library-management-system` folder, not the zip file and not only `README.md`.
4. Open `src/main/java/com/example/library/DesktopApp.java`.
5. Click the green run button next to `public static void main`.

If `src` is not shown, change the left panel from **Project Files** to **Project**, or right-click `src/main/java` and choose **Mark Directory as > Sources Root**.

## Run on Windows

```bat
cd library-management-system
run.bat
```

## Manual Compile and Run

```sh
cd library-management-system
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.example.library.Main
```

For the desktop app, use:

```sh
java -cp out com.example.library.DesktopApp
```

## Project Structure

```text
library-management-system/
  src/main/java/com/example/library/
    Main.java
    DesktopApp.java
    LibraryService.java
    DataStore.java
    Book.java
    Member.java
  data/
  run.sh
  run.bat
  README.md
```
