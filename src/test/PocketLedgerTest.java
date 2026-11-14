import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.math.BigDecimal;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class PocketLedgerTest {
    @TempDir Path tempDir;

    @Test void savesAndReadsExpenses() throws Exception {
        PocketLedger ledger = new PocketLedger(tempDir.resolve("expenses.csv"));
        ledger.add(new BigDecimal("125.50"), "Food", "Lunch with friends");
        assertEquals(1, ledger.list().size());
        assertEquals(new BigDecimal("125.50"), ledger.total());
    }

    @Test void calculatesCategoryTotals() throws Exception {
        PocketLedger ledger = new PocketLedger(tempDir.resolve("expenses.csv"));
        ledger.add(new BigDecimal("100"), "Food", "Lunch");
        ledger.add(new BigDecimal("50.25"), "Food", "Coffee");
        ledger.add(new BigDecimal("200"), "Travel", "Bus ticket");
        assertEquals(new BigDecimal("150.25"), ledger.totalsByCategory().get("Food"));
        assertEquals(new BigDecimal("350.25"), ledger.total());
    }

    @Test void deletesExistingExpense() throws Exception {
        PocketLedger ledger = new PocketLedger(tempDir.resolve("expenses.csv"));
        var expense = ledger.add(new BigDecimal("75"), "Travel", "Metro");
        assertTrue(ledger.delete(expense.id()));
        assertFalse(ledger.delete(expense.id()));
        assertTrue(ledger.list().isEmpty());
    }

    @Test void rejectsNonPositiveAmounts() {
        PocketLedger ledger = new PocketLedger(tempDir.resolve("expenses.csv"));
        assertThrows(IllegalArgumentException.class,
                () -> ledger.add(new BigDecimal("0"), "Food", "Lunch"));
    }

    @Test void preservesCommasAndQuotesInDescription() throws Exception {
        PocketLedger ledger = new PocketLedger(tempDir.resolve("expenses.csv"));
        ledger.add(new BigDecimal("42"), "Food", "Tea, "large" cup");
        assertEquals("Tea, "large" cup", ledger.list().get(0).description());
    }
}
