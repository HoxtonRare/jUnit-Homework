package exercise.article;

import exercise.worker.WorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkerImplTest {

    private WorkerImpl worker;
    @Mock
    private Library lib;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        worker = new WorkerImpl(lib);

    }

    @Test
    void getCatalogWithTitlesInAlphabeticalOrder() {
        List<String> titles = Arrays.asList("Чек", "Алфавитный", "Порядок");
        when(lib.getAllTitles()).thenReturn((List<String>) titles);
        String catalog = worker.getCatalog();
        String expectedCatalog = "Список доступных статей:\n" +
                "    Алфавитный\n" +
                "    Порядок\n" +
                "    Чек\n";
        assertEquals(expectedCatalog, catalog);
    }

    @Test
    void getCatalogWithoutTitles() {
        String catalog = worker.getCatalog();
        String expectedCatalog = "Список доступных статей:\n";
        assertEquals(expectedCatalog, catalog);
    }

    @Test
    void PrepareArticlesWithCorrectData() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Ворона и Лисица", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(2, preparedArticles.size());
        assertEquals(articles, preparedArticles);
    }

    @Test
    void PrepareArticlesWithWrongData() {
        List<Article> articles = new ArrayList<>();
        LocalDate date = null;
        articles.add(new Article("Ворона и Лисица", "", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Волк и ягнёнок", "Басня", "", LocalDate.now()));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(0, preparedArticles.size());
    }

    @Test
    void PrepareArticlesWithCheckForCorrectDate() {
        List<Article> articles = new ArrayList<>();
        LocalDate date = null;
        articles.add(new Article("Ворона и Лисица", "Басня", "Иван Андреевич Крылов", date));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", date));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(2, preparedArticles.size());
        for (Article article : preparedArticles) {
            assertEquals(LocalDate.now(), article.getCreationDate());
        }
    }

    @Test
    public void AddNewArticlesWithValidArticles() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Ворона и Лисица", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        worker.addNewArticles(articles);
        Mockito.verify(lib, times(1)).store(anyInt(), anyList());
    }

    @Test
    public void AddNewArticlesWithNullArticlesAndThrowsException() throws NullPointerException {
        Throwable thrown = assertThrows(NullPointerException.class, () -> worker.addNewArticles(null));
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void AddNewArticlesWithEmptyArticles() {
        List<Article> articles = new ArrayList<>();
        worker.addNewArticles(articles);
        Mockito.verify(lib, never()).store(anyInt(), anyList());
    }

    @Test
    public void PreparedNewArticlesWithIdenticalTitles() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(1, preparedArticles.size());
    }
}
