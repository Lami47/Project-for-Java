import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class HelloWorld {
    public static void main(String[] args) {
        LibraryDatabase libraryDatabase = new LibraryDatabase();
        Scanner scanner = new Scanner(System.in);
        
        boolean exit = false;

        while (!exit) {
            System.out.println("\n -- Menu --");
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("\nChoose an option: ");

            int choice = getValidInput(scanner);
            //1st Menu when starting this Java doc
            switch (choice) {
                case 1 -> {
                    // Login
                    System.out.println("\nLogin to your account");
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    User user = libraryDatabase.getUserDatabase().login(email, password);
                    if (user != null) {
                        // Proceed with the library functionality
                        libraryFunctionality(user, libraryDatabase.getAvailableBooks(), scanner);
                    } else {
                        System.out.println("Invalid email or password.");
                    }
                }
                case 2 -> {
                    // Registration
                    System.out.println("Register a new user");
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();

                    // Check if user already exists
                    if (libraryDatabase.getUserDatabase().userExists(email)) {
                        System.out.println("User already exists with this email.");
                    } else {
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter date of birth (YYYY-MM-DD): ");
                        String birthDay = scanner.nextLine();

                        libraryDatabase.getUserDatabase().registerUser(name, email, password, birthDay);
                        System.out.println("User registered successfully.");
                    }
                }
                case 3 -> {
                    // Closes the app
                    exit = true;
                    System.out.println("Exiting the program.");
                }
                default -> System.out.println("Invalid option. Please choose again.");
            }
        }
        
        scanner.close();
    }

    private static int getValidInput(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static void libraryFunctionality(User user, List<Book> availableBooks, Scanner scanner) {
        boolean exit = false;
        //2nd Menu once you are logged in
        while (!exit) {
            System.out.println("\n -- Menu --");
            System.out.println("\n1. View User Info");
            System.out.println("2. Borrow a Book");
            System.out.println("3. View Borrowed Books");
            System.out.println("4. Return Books");
            System.out.println("5. Logout");

            System.out.print("\nChoose an option: ");
            int choice = getValidInput(scanner);
            //options
            switch (choice) {
                // display users info

                case 1 -> System.out.printf("\nName : %s\nD.O.B: %s\nAge  : %d years old\n", user.getName(), user.getBirthDay(), user.age());
                // Displays the books in the library
                //case 2 -> addOrRemoveBooks() --> need a way to save books to a dat file
                case 2 -> borrowBooks(user, availableBooks, scanner);
                // Displays the books the user has taken out
                case 3 -> {
                    List<Book> borrowedBooks = user.getBorrowedBooks();
                    if (borrowedBooks.isEmpty()) {
                        System.out.println("\nYou have not borrowed any books.");
                    } else {
                        System.out.println("\nYou have borrowed the following books:");
                        for (Book book : borrowedBooks) {
                            System.out.printf("- \"%s\" by %s\n", book.getTitle(), book.getAuthor());
                        }
                    }
                }
                // Displays the books the user can return
                case 4 -> returnBooks(user, scanner);
                // Logs out
                case 5 -> {
                    exit = true;
                    System.out.println("You've been logout");
                }
                default -> System.out.println("\nInvalid option. Please choose again.");
            }
        }
    }

    private static void borrowBooks(User user, List<Book> availableBooks, Scanner scanner) {
        if (user.getBorrowedBooks().size() >= 3) {
            System.out.println("\nYou cannot borrow more than 3 books at a time. Please return some books first.");
            return;
        }

        System.out.println("Available books:\n");
        for (int i = 0; i < availableBooks.size(); i++) {
            
            System.out.printf("%d. %s\n", i + 1, availableBooks.get(i).toString());
        }

        System.out.print("\n(Separate with a comma, a user can borrow a maximum of 3 books at any given time)\nEnter the numbers of the books you want to borrow: ");
        String input = scanner.nextLine();
        String[] selections = input.split(",");

        List<Book> borrowedBooks = new ArrayList<>();

        for (String selection : selections) {
            try {
                int index = Integer.parseInt(selection.trim()) - 1; // Convert to zero-based index
                if (index >= 0 && index < availableBooks.size()) {
                    Book bookToBorrow = availableBooks.get(index);
                    if (bookToBorrow.isAvailable()) {
                        borrowedBooks.add(bookToBorrow);
                    } else {
                        System.out.printf("\nThe book \"%s\" by %s is currently unavailable.\n", bookToBorrow.getTitle(), bookToBorrow.getAuthor());
                    }
                } else {
                    System.out.println("\nInvalid selection: " + (index + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input: " + selection);
            }
        }

        for (Book book : borrowedBooks) {
            System.out.println("");
            user.borrow(book); //Displays the book the user borrowed
            book.borrowBook(); // Call borrowBook to set availabilty to false(cause the book is taken out now)
        }
    }

    private static void returnBooks(User user, Scanner scanner) {
        System.out.println("\nYou have borrowed the following books:");
        List<Book> borrowed = user.getBorrowedBooks();
        if (borrowed.isEmpty()) {
            System.out.println("You have not borrowed any books.");
            return;
        }

        for (int i = 0; i < borrowed.size();i++) {
            Book book = borrowed.get(i);
            System.out.printf("%d. \"%s\" by %s\n", i + 1, book.getTitle(), book.getAuthor());
        }
        
        System.out.print("\nEnter the numbers of the books you want to return (Separate books with a comma): ");
        String input = scanner.nextLine();
        String[] selections = input.split(",");

        for (String selection : selections) {
            try {
                int indexToReturn = Integer.parseInt(selection.trim()) - 1; // Convert to zero-based index
                if (indexToReturn >= 0 && indexToReturn < borrowed.size()) {
                    user.returnBook(borrowed.get(indexToReturn)); //Displayes book is returned
                    borrowed.get(indexToReturn).returnBook(); 
                } else {
                    System.out.println("Invalid selection: " + (indexToReturn + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + selection);
            }
        }
    }
}
