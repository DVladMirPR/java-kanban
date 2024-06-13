package ru.yandex.manager.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        this(id, title, description, Status.NEW, epicId);
    }

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


    @Override
    public String toString() {
        return "Subtask{" + "epicId=" + epicId + ", id=" + getId() + ", title='" + getTitle() + '\'' + ", description='" + getDescription() + '\'' + '}';
    }
}