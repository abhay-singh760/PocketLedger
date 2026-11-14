import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A compact, local-file personal expense tracker. */
public class PocketLedger {
    private final Path storageFile;

    public record Expense(long id, LocalDate date, BigDecimal amount,
                          String category, String description) {}

    public PocketLedger(Path storageFile) {
        this.storageFile = storageFile;
    }

    public Expense add(BigDecimal amount, String category, String description) throws IOException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Category cannot be empty.");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description cannot be empty.");

        List<Expense> expenses = list();
        long nextId = expenses.stream().mapToLong(Expense::id).max().orElse(0) + 1;
        Expense expense = new Expense(nextId, LocalDate.now(),
                amount.setScale(2, RoundingMode.HALF_UP), category.trim(), description.trim());
        expenses.add(expense);
        save(expenses);
        return expense;
    }

    public List<Expense> list() throws IOException {
        if (!Files.exists(storageFile)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(storageFile, StandardCharsets.UTF_8);
        List<Expense> expenses = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            List<String> fields = parseCsvLine(lines.get(i));
            if (fields.size() != 5) throw new IOException("Invalid CSV record at line " + (i + 1));
            try {
                expenses.add(new Expense(Long.parseLong(fields.get(0)), LocalDate.parse(fields.get(1)),
                        new BigDecimal(fields.get(2)), fields.get(3), fields.get(4)));
            } catch (RuntimeException ex) {
                throw new IOException("Invalid expense data at line " + (i + 1), ex);
            }
        }
        expenses.sort(Comparator.comparingLong(Expense::id));
        return expenses;
    }

    public boolean delete(long id) throws IOException {
        List<Expense> expenses = list();
        boolean removed = expenses.removeIf(expense -> expense.id() == id);
        if (removed) save(expenses);
        return removed;
    }

    public BigDecimal total() throws IOException {
        return list().stream().map(Expense::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> totalsByCategory() throws IOException {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (Expense expense : list())
            totals.merge(expense.category(), expense.amount(), BigDecimal::add);
        return totals;
    }

    private void save(List<Expense> expenses) throws IOException {
        Path parent = storageFile.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        List<String> lines = new ArrayList<>();
        lines.add("id,date,amount,category,description");
        for (Expense e : expenses)
            lines.add(e.id() + "," + e.date() + "," + e.amount() + ","
                    + csv(e.category()) + "," + csv(e.description()));
        Files.write(storageFile, lines, StandardCharsets.UTF_8);
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    static List<String> parseCsvLine(String line) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else quoted = !quoted;
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else field.append(c);
        }
        if (quoted) throw new IOException("Unclosed quote in CSV record.");
        fields.add(field.toString());
        return fields;
    }

    private static void usage() {
        System.out.println("PocketLedger - personal expense tracker\n"
                + "Commands:\n"
                + "  add <amount> <category> <description>  Add an expense\n"
                + "  list                                   List expenses\n"
                + "  summary                                Show spending totals\n"
                + "  delete <id>                            Delete an expense\n"
                + "  help                                   Show this help\n\n"
                + "Example: java -jar target/pocketledger-1.0.0.jar add 125.50 Food \"Lunch with friends\"\n"
                + "Data is stored in ./data/expenses.csv");
    }

    public static void main(String[] args) {
        PocketLedger ledger = new PocketLedger(Path.of("data", "expenses.csv"));
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            usage();
            return;
        }
        try {
            switch (args[0].toLowerCase()) {
                case "add" -> {
                    if (args.length < 4) throw new IllegalArgumentException(
                            "Usage: add <amount> <category> <description>");
                    BigDecimal amount = new BigDecimal(args[1]);
                    String description = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));
                    Expense added = ledger.add(amount, args[2], description);
                    System.out.println("Expense added successfully (ID: " + added.id() + ").");
                }
                case "list" -> {
                    List<Expense> expenses = ledger.list();
                    if (expenses.isEmpty()) System.out.println("No expenses recorded yet.");
                    else {
                        System.out.printf("%-5s %-12s %-12s %-16s %s%n",
                                "ID", "DATE", "AMOUNT", "CATEGORY", "DESCRIPTION");
                        for (Expense e : expenses)
                            System.out.printf("%-5d %-12s %-12s %-16s %s%n",
                                    e.id(), e.date(), e.amount(), e.category(), e.description());
                    }
                }
                case "summary" -> {
                    System.out.println("EXPENSE SUMMARY");
                    ledger.totalsByCategory().forEach((category, amount) ->
                            System.out.printf("  %-18s %10s%n", category, amount));
                    System.out.println("------------------------------");
                    System.out.println("  TOTAL             " + ledger.total());
                }
                case "delete" -> {
                    if (args.length != 2) throw new IllegalArgumentException("Usage: delete <id>");
                    if (ledger.delete(Long.parseLong(args[1]))) System.out.println("Expense deleted.");
                    else System.out.println("No expense found with ID " + args[1] + ".");
                }
                default -> {
                    usage();
                    System.exit(2);
                }
            }
        } catch (NumberFormatException ex) {
            System.err.println("Invalid number. Enter a valid amount or expense ID.");
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(1);
        }
    }
}
