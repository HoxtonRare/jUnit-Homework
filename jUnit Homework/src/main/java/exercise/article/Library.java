package exercise.article;

import java.util.List;

public interface Library {
    //Добавляет новые статьи, но не обновляет каталог
    void store(int year, List<Article> articles);

    // Обновляем каталог, чтобы в нем отобразились загруженные статьи
    void updateCatalog();

    //Возвращает список названий статей
    List<String> getAllTitles();
}
