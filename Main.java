import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<Book> library = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static int nextId = 1;

    public static void main(String[] args) {
        seedInitialData();
        boolean running = true;

        System.out.println("============================================================================================================");
        System.out.println("                                   KÜTÜPHANE YÖNETİM SİSTEMİ BAŞLATILDI                                     ");
        System.out.println("============================================================================================================");

        while (running) {
            printMenu();
            int choice = readInt("Seçiminiz: ");

            switch (choice) {
                case 1 -> listAllBooks();
                case 2 -> addNewBook();
                case 3 -> searchBooks();
                case 4 -> borrowBook();
                case 5 -> returnBook();
                case 6 -> deleteBook();
                case 0 -> {
                    System.out.println("\nSistem güvenli bir şekilde kapatılıyor. İyi günler!");
                    running = false;
                }
                default -> System.out.println("\n[!] Hatalı seçim. Lütfen menüdeki (0-6) numara aralığından seçim yapın.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n-------------------------------------------------- MENÜ --------------------------------------------------");
        System.out.println("1 - Tüm Kitapları Listele");
        System.out.println("2 - Yeni Kitap Ekle");
        System.out.println("3 - Kitap Ara (İsim veya Yazar)");
        System.out.println("4 - Kitap Ödünç Al");
        System.out.println("5 - Kitap İade Et");
        System.out.println("6 - Kütüphaneden Kitap Sil");
        System.out.println("0 - Çıkış");
        System.out.println("----------------------------------------------------------------------------------------------------------");
    }

    private static void listAllBooks() {
        if (library.isEmpty()) {
            System.out.println("\n[!] Kütüphanede henüz kayıtlı kitap bulunmuyor.");
            return;
        }

        printTableHeader();
        int availableCount = 0;
        for (Book book : library) {
            System.out.println(book);
            if (!book.isBorrowed()) {
                availableCount++;
            }
        }
        printTableFooter();
        System.out.printf("Toplam Kitap: %d | Rafta: %d | Ödünçte: %d%n",
                library.size(), availableCount, (library.size() - availableCount));
    }

    private static void addNewBook() {
        System.out.println("\n--- Yeni Kitap Ekle ---");
        String title = readNonEmptyString("Kitap Adı: ");
        String author = readNonEmptyString("Yazar Adı: ");

        if (isDuplicateBook(title, author)) {
            System.out.println("\n[!] HATA: Bu yazara ait aynı isimli kitap zaten kütüphanede mevcut!");
            return;
        }

        int pageCount;
        while (true) {
            pageCount = readInt("Sayfa Sayısı: ");
            if (pageCount > 0 && pageCount <= 10000) {
                break;
            }
            System.out.println("[!] Sayfa sayısı 1 ile 10.000 arasında mantıklı bir değer olmalıdır.");
        }

        Book newBook = new Book(nextId++, title, author, pageCount);
        library.add(newBook);
        System.out.println("\n[+] '" + title + "' başarıyla kütüphaneye eklendi. (Atanan ID: " + newBook.getId() + ")");
    }

    private static void searchBooks() {
        if (library.isEmpty()) {
            System.out.println("\n[!] Kütüphane boş olduğu için arama yapılamaz.");
            return;
        }

        String keyword = readNonEmptyString("\nAramak istediğiniz kitap veya yazar adı: ").toLowerCase();
        List<Book> results = new ArrayList<>();

        for (Book book : library) {
            if (book.getTitle().toLowerCase().contains(keyword) ||
                    book.getAuthor().toLowerCase().contains(keyword)) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            System.out.println("\n[!] '" + keyword + "' ile eşleşen hiçbir kayıt bulunamadı.");
        } else {
            System.out.println("\nBulunan Eşleşmeler (" + results.size() + " adet):");
            printTableHeader();
            for (Book book : results) {
                System.out.println(book);
            }
            printTableFooter();
        }
    }

    private static void borrowBook() {
        if (library.isEmpty()) {
            System.out.println("\n[!] Kütüphanede ödünç alınabilecek kitap yok.");
            return;
        }

        int id = readInt("\nÖdünç almak istediğiniz kitabın ID numarası: ");
        Book book = findBookById(id);

        if (book == null) {
            System.out.println("\n[!] HATA: " + id + " ID numarasına sahip bir kitap bulunamadı.");
            return;
        }

        if (book.isBorrowed()) {
            System.out.println("\n[!] UYARI: '" + book.getTitle() + "' şu anda zaten ödünçte! (Alan kişi: " + book.getBorrowedBy() + ")");
            return;
        }

        String borrower = readNonEmptyString("Kitabı ödünç alan kişinin Adı Soyadı: ");
        book.borrowBook(borrower);
        System.out.println("\n[+] '" + book.getTitle() + "' adlı kitap " + borrower + " adına ödünç verildi.");
    }

    private static void returnBook() {
        if (library.isEmpty()) {
            System.out.println("\n[!] Kütüphane kaydı boş.");
            return;
        }

        int id = readInt("\nİade etmek istediğiniz kitabın ID numarası: ");
        Book book = findBookById(id);

        if (book == null) {
            System.out.println("\n[!] HATA: " + id + " ID numarasına sahip bir kitap bulunamadı.");
            return;
        }

        if (!book.isBorrowed()) {
            System.out.println("\n[!] UYARI: '" + book.getTitle() + "' zaten kütüphane rafında, ödünç verilmemiş.");
            return;
        }

        String previousBorrower = book.getBorrowedBy();
        book.returnBook();
        System.out.println("\n[+] '" + book.getTitle() + "' (" + previousBorrower + " tarafından) başarıyla kütüphaneye iade edildi.");
    }

    private static void deleteBook() {
        if (library.isEmpty()) {
            System.out.println("\n[!] Silinecek kitap bulunmuyor.");
            return;
        }

        int id = readInt("\nKütüphaneden silmek istediğiniz kitabın ID numarası: ");
        Book book = findBookById(id);

        if (book == null) {
            System.out.println("\n[!] HATA: " + id + " ID numarasına sahip bir kitap bulunamadı.");
            return;
        }

        if (book.isBorrowed()) {
            System.out.println("\n[!] GÜVENLİK ENGELİ: '" + book.getTitle() + "' şu anda " + book.getBorrowedBy() + " adlı kişide ödünçte olduğu için silinemez! Önce iade edilmelidir.");
            return;
        }

        library.remove(book);
        System.out.println("\n[-] '" + book.getTitle() + "' kütüphane envanterinden kalıcı olarak silindi.");
    }

    private static Book findBookById(int id) {
        for (Book book : library) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    private static boolean isDuplicateBook(String title, String author) {
        for (Book book : library) {
            if (book.getTitle().equalsIgnoreCase(title) && book.getAuthor().equalsIgnoreCase(author)) {
                return true;
            }
        }
        return false;
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[!] Bu alan boş bırakılamaz. Lütfen geçerli bir metin girin.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Geçersiz giriş! Lütfen sadece sayısal bir değer girin.");
            }
        }
    }

    private static void printTableHeader() {
        System.out.println("\n+------+------------------------------+------------------------+--------+----------------------------------+");
        System.out.printf("| %-4s | %-28s | %-22s | %-6s | %-32s |%n", "ID", "KİTAP ADI", "YAZAR", "SAYFA", "DURUM");
        System.out.println("+------+------------------------------+------------------------+--------+----------------------------------+");
    }

    private static void printTableFooter() {
        System.out.println("+------+------------------------------+------------------------+--------+----------------------------------+");
    }

    private static void seedInitialData() {
        library.add(new Book(nextId++, "Arsène Lupin: Kibar Hırsız", "Maurice Leblanc", 240));
        library.add(new Book(nextId++, "813", "Maurice Leblanc", 416));
        library.add(new Book(nextId++, "Kristal Tıpa", "Maurice Leblanc", 304));
        library.add(new Book(nextId++, "Nutuk", "Mustafa Kemal Atatürk", 658));
    }
}