import { useMemo, useState } from "react";
import { useTasks } from "./hooks/useTasks";
import StatusFilter from "./components/StatusFilter";
import TaskList from "./components/TaskList";
import Modal from "./components/Modal";
import TaskForm from "./components/TaskForm";
import ErrorBanner from "./components/ErrorBanner";
import ConfirmDeleteDialog from "./components/ConfirmDeleteDialog";
import { STATUS_ORDER } from "./utils/status";

export default function App() {
  const {
    tasks,
    isLoading,
    error,
    statusFilter,
    setStatusFilter,
    createTask,
    updateTask,
    deleteTask,
    refresh,
  } = useTasks();

  const [editingTask, setEditingTask] = useState(null); // null = fermé, {} = création, task = édition
  const [taskToDelete, setTaskToDelete] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const counts = useMemo(() => {
    const base = { all: tasks.length };
    STATUS_ORDER.forEach((s) => (base[s] = tasks.filter((t) => t.status === s).length));
    return base;
  }, [tasks]);

  async function handleFormSubmit(payload) {
    if (editingTask?.id) {
      await updateTask(editingTask.id, payload);
    } else {
      await createTask(payload);
    }
    setEditingTask(null);
  }

  async function handleConfirmDelete() {
    setIsDeleting(true);
    try {
      await deleteTask(taskToDelete.id);
      setTaskToDelete(null);
    } catch (err) {
      // On laisse la modale ouverte et on affiche l'erreur au-dessus de la liste.
      setTaskToDelete(null);
      refresh();
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <div className="page">
      <header className="page__header">
        <div>
          <p className="page__eyebrow">Registre des opérations</p>
          <h1 className="page__title">Dispatch</h1>
          <p className="page__subtitle">Suivi des tâches de l'équipe, statut par statut.</p>
        </div>
        <button type="button" className="btn btn--primary" onClick={() => setEditingTask({})}>
          + Nouvelle tâche
        </button>
      </header>

      <ErrorBanner message={error} onRetry={refresh} />

      <StatusFilter value={statusFilter} onChange={setStatusFilter} counts={counts} />

      <main>
        <TaskList
          tasks={tasks}
          isLoading={isLoading}
          onEdit={(task) => setEditingTask(task)}
          onDelete={(task) => setTaskToDelete(task)}
        />
      </main>

      {editingTask && (
        <Modal
          title={editingTask.id ? "Modifier la tâche" : "Nouvelle tâche"}
          onClose={() => setEditingTask(null)}
        >
          <TaskForm
            initialTask={editingTask.id ? editingTask : null}
            onSubmit={handleFormSubmit}
            onCancel={() => setEditingTask(null)}
          />
        </Modal>
      )}

      <ConfirmDeleteDialog
        task={taskToDelete}
        onConfirm={handleConfirmDelete}
        onCancel={() => setTaskToDelete(null)}
        isDeleting={isDeleting}
      />
    </div>
  );
}
