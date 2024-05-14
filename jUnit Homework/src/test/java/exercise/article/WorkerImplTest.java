package exercise.article;

import exercise.worker.WorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Вывод каталога в алфавитном порядке")
    public void testGetCatalogWithTitlesInAlphabeticalOrder() {
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
    @DisplayName("Вывод пустого каталога")
    public void testGetCatalogWithoutTitles() {
        String catalog = worker.getCatalog();
        String expectedCatalog = "Список доступных статей:\n";
        assertEquals(expectedCatalog, catalog);
    }

    @Test
    @DisplayName("Подготовка статей с корректными данными")
    public void testPrepareArticlesWithCorrectData() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Ворона и Лисица", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(2, preparedArticles.size());
        assertEquals(articles, preparedArticles);
    }

    @Test
    @DisplayName("Подготовка статей с одним незаполненным обязательным полем")
    public void testPrepareArticlesWithWrongData() {
        List<Article> articles = new ArrayList<>();
        LocalDate date = null;
        articles.add(new Article("Ворона и Лисица", "", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Волк и ягнёнок", "Басня", "", LocalDate.now()));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(0, preparedArticles.size());
    }

    @Test
    @DisplayName("Автоматическое заполнение поля Дата, в случае если это поле было задано как null")
    public void testPrepareArticlesWithCheckForCorrectDate() {
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
    @DisplayName("Срабатывание метода store() только при добавлении статей работником")
    public void testAddNewArticlesWithValidArticles() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Ворона и Лисица", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        worker.addNewArticles(articles);
        Mockito.verify(lib, times(1)).store(anyInt(), anyList());
    }

    @Test
    @DisplayName("Срабатывание исключения, в случае добавления работником вместо статьи, нулевого указателя")
    public void shouldThrowsExceptionWhenAddNewArticlesWithNull() throws NullPointerException {
        try {
            worker.addNewArticles(null);
        } catch (NullPointerException e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    @DisplayName("Отсутствие вызова метода store(), в случае добавления работником пустого списка статей")
    public void testAddNewArticlesWithEmptyListOfArticles() {
        List<Article> articles = new ArrayList<>();
        worker.addNewArticles(articles);
        Mockito.verify(lib, never()).store(anyInt(), anyList());
    }

    @Test
    @DisplayName("Защита от повторяющихся названий статей в каталоге после работы работника")
    public void shouldCheckForDuplicateArticleTitles() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        articles.add(new Article("Мартышка и очки", "Басня", "Иван Андреевич Крылов", LocalDate.now()));
        List<Article> preparedArticles = worker.prepareArticles(articles);
        assertEquals(1, preparedArticles.size());
    }
}
