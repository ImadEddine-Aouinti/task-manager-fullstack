import { useCallback, useEffect, useState } from "react";
import { taskApi, ApiError } from "../api/taskApi";

export function useTasks() {
  const [tasks, setTasks] = useState([]);
  const [statusFilter, setStatusFilter] = useState(null); // null = toutes
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchTasks = useCallback(async (status) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await taskApi.getAll(status);
      setTasks(data);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Impossible de charger les tâches.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTasks(statusFilter);
  }, [statusFilter, fetchTasks]);

  const createTask = useCallback(
    async (payload) => {
      const created = await taskApi.create(payload);
      // Recharge la liste pour respecter le filtre courant plutôt que de deviner l'ordre du serveur.
      await fetchTasks(statusFilter);
      return created;
    },
    [fetchTasks, statusFilter]
  );

  const updateTask = useCallback(
    async (id, payload) => {
      const updated = await taskApi.update(id, payload);
      await fetchTasks(statusFilter);
      return updated;
    },
    [fetchTasks, statusFilter]
  );

  const deleteTask = useCallback(
    async (id) => {
      await taskApi.remove(id);
      setTasks((current) => current.filter((task) => task.id !== id));
    },
    []
  );

  return {
    tasks,
    isLoading,
    error,
    statusFilter,
    setStatusFilter,
    createTask,
    updateTask,
    deleteTask,
    refresh: () => fetchTasks(statusFilter),
  };
}
