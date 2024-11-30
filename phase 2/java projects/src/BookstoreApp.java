public class BookstoreApp {
    public static void main(String[] args) {
        DatabaseOperations dbOps = new DatabaseOperations();
        BookstoreGUI gui = new BookstoreGUI(dbOps);
        gui.createAndShowGUI();
    }
}
