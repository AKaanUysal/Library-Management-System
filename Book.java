import java.time.LocalDate;

public class Book {
    private final int id;
    private final String title;
    private final String author;
    private final int pageCount;
    private boolean isBorrowed;
    private String borrowedBy;
    private LocalDate borrowDate;

    public Book(int id, String title, String author, int pageCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.isBorrowed = false;
        this.borrowedBy = null;
        this.borrowDate = null;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public String getBorrowedBy() {
        return borrowedBy;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void borrowBook(String borrowerName) {
        this.isBorrowed = true;
        this.borrowedBy = borrowerName;
        this.borrowDate = LocalDate.now();
    }

    public void returnBook() {
        this.isBorrowed = false;
        this.borrowedBy = null;
        this.borrowDate = null;
    }

    @Override
    public String toString() {
        String status = isBorrowed
                ? String.format("Ödünçte (%s - %s)", borrowedBy, borrowDate)
                : "Rafta (Müsait)";
        return String.format("| %-4d | %-28s | %-22s | %-6d | %-32s |",
                id, truncate(title, 28), truncate(author, 22), pageCount, status);
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}