package service;

public interface Managers {
//    public static InMemoryTaskManager getDefault() {
//        return new InMemoryTaskManager();
//    }

    // по первому методу ообще вопрос, это осколок от прошлого тз ,сейчас оно есть только в тестах, смысла в этом методе не



    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
