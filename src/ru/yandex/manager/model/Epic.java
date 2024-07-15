package ru.yandex.manager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subTasks = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW, Duration.ZERO, LocalDateTime.now());
    }

    public Epic(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
    }

    public void addSubtask(Subtask subtask) {
        subTasks.add(subtask);
        calculateEpicTime();
    }

    public void removeSubtask(Subtask subtask) {
        subTasks.remove(subtask);
        calculateEpicTime();
    }

    public void updateSubtask(Subtask subtask) {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId() == subtask.getId()) {
                subTasks.set(i, subtask);
                break;
            }
        }
        calculateEpicTime();
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subTasks);
    }

    private void calculateEpicTime() {
        if (subTasks.isEmpty()) {
            this.setStartTime(LocalDateTime.now());
            this.setDuration(Duration.ZERO);
        } else {
            LocalDateTime earliestStart = LocalDateTime.MAX;
            LocalDateTime latestEnd = LocalDateTime.MIN;

            for (Subtask subtask : subTasks) {
                if (subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(latestEnd)) {
                    latestEnd = subtask.getEndTime();
                }
            }

            this.setStartTime(earliestStart);
            this.setDuration(Duration.between(earliestStart, latestEnd));
        }
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + getId() + ", title='" + getTitle() + '\'' + ", description='" + getDescription() + '\'' + ", status=" + getStatus() + ", duration=" + getDuration() + ", startTime=" + getStartTime() + ", endTime=" + getEndTime() + ", subTasks=" + subTasks + '}';
    }
}
