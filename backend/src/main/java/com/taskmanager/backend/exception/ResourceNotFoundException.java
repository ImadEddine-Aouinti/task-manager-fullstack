package com.taskmanager.backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forTask(Long id) {
        return new ResourceNotFoundException("Aucune tâche trouvée avec l'id : " + id);
    }
}
