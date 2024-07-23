package ru.yandex.manager.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        super(id, title, description, Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
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
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}