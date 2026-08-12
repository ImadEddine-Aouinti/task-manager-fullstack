import TaskCard from "./TaskCard";

export default function TaskList({ tasks, isLoading, onEdit, onDelete }) {
  if (isLoading) {
    return (
      <div className="state-panel">
        <p>Chargement des tâches…</p>
      </div>
    );
  }

  if (tasks.length === 0) {
    return (
      <div className="state-panel">
        <p className="state-panel__title">Aucune tâche ici.</p>
        <p>Créez-en une avec le bouton « Nouvelle tâche » pour commencer.</p>
      </div>
    );
  }

  return (
    <div className="ticket-grid">
      {tasks.map((task) => (
        <TaskCard key={task.id} task={task} onEdit={onEdit} onDelete={onDelete} />
      ))}
    </div>
  );
}
