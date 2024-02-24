package taskManager;

import java.util.ArrayList;

class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
}
