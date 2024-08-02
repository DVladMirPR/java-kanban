package ru.yandex.manager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subTasks;
    private LocalDateTime endTime;

    public Epic(Integer id, String title, String description) {
        super(id, title, description, Status.NEW, Duration.ZERO, LocalDateTime.now());
        this.endTime = LocalDateTime.now();
        this.subTasks = new ArrayList<>();
    }

    public Epic(Integer id, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, title, description, status, duration, startTime);
        this.endTime = startTime.plus(duration);
        this.subTasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subTasks.add(subtask);
        calculateEpicTime();
        updateEpicStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subTasks.remove(subtask);
        calculateEpicTime();
        updateEpicStatus();
    }

    public void updateSubtask(Subtask subtask) {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId() == subtask.getId()) {
                subTasks.set(i, subtask);
                break;
            }
        }
        calculateEpicTime();
        updateEpicStatus();
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        if (subTasks == null) {
            subTasks = new ArrayList<>();
        }
        return subTasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateEpicStatus() {
        long countNew = subTasks.stream().filter(subtask -> subtask.getStatus() == Status.NEW).count();
        long countDone = subTasks.stream().filter(subtask -> subtask.getStatus() == Status.DONE).count();
        int size = subTasks.size();
        if (countNew == size) {
            this.setStatus(Status.NEW);
        } else if (countDone == size) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    private void calculateEpicTime() {
        if (subTasks.isEmpty()) {
            this.setStartTime(LocalDateTime.now());
            this.setDuration(Duration.ZERO);
            this.endTime = LocalDateTime.now();
        } else {
            LocalDateTime earliestStart = LocalDateTime.MAX;
            LocalDateTime latestEnd = LocalDateTime.MIN;
            Duration totalDuration = Duration.ZERO;

            for (Subtask subtask : subTasks) {
                if (subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(latestEnd)) {
                    latestEnd = subtask.getEndTime();
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }

            this.setStartTime(earliestStart);
            this.setDuration(totalDuration);
            this.endTime = latestEnd;
        }
    }

    @Override
    public String toString() {
        return "Epic{" + "id=" + getId() + ", title='" + getTitle() + '\'' + ", description='" + getDescription() + '\'' + ", status=" + getStatus() + ", duration=" + getDuration() + ", startTime=" + getStartTime() + ", endTime=" + getEndTime() + ", subTasks=" + subTasks + '}';
    }
}