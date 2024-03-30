package taskManagerSprint4.service;

public class IdGenerator {
    private int currentId;

    public int generateId() {
        return currentId++;
    }
}