package ru.yandex.manager.model;

public class Epic extends Task {
    // Конструктор с параметрами
    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}
