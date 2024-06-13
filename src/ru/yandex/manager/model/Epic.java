package ru.yandex.manager.model;

public class Epic extends Task {
    public Epic(int id, String title, String description) {
        this(id, title, description, Status.NEW);
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    @Override
    public Integer getEpicId() {
        return null;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + getId() + ", title='" + getTitle() + '\'' + ", description='" + getDescription() + '\'' + '}';
    }
}