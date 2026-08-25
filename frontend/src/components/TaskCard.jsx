import { STATUS_META, formatDate, ticketRef } from "../utils/status";

export default function TaskCard({ task, onEdit, onDelete }) {
  const meta = STATUS_META[task.status] ?? STATUS_META.TODO;

  return (
    <article className={`ticket tone-${meta.tone}`}>
      <div className="ticket__perforation" aria-hidden="true" />
      <div className="ticket__body">
        <header className="ticket__header">
          <span className="ticket__ref">{ticketRef(task.id)}</span>
          <span className="ticket__status">{meta.short}</span>
        </header>

        <h3 className="ticket__title">{task.title}</h3>

        {task.description && <p className="ticket__description">{task.description}</p>}

        <dl className="ticket__meta">
          {task.dueDate && (
            <div>
              <dt>Échéance</dt>
              <dd>{formatDate(task.dueDate)}</dd>
            </div>
          )}
          <div>
            <dt>Créée le</dt>
            <dd>{formatDate(task.createdAt)}</dd>
          </div>
        </dl>

        <footer className="ticket__actions">
          <button type="button" className="btn btn--ghost" onClick={() => onEdit(task)}>
            Modifier
          </button>
          <button type="button" className="btn btn--danger-ghost" onClick={() => onDelete(task)}>
            Supprimer
          </button>
        </footer>
      </div>
    </article>
  );
}
