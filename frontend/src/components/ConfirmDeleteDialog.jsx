import Modal from "./Modal";

export default function ConfirmDeleteDialog({ task, onConfirm, onCancel, isDeleting }) {
  if (!task) return null;

  return (
    <Modal title="Supprimer cette tâche ?" onClose={onCancel}>
      <p className="confirm-dialog__text">
        La tâche <strong>« {task.title} »</strong> sera supprimée définitivement. Cette action est
        irréversible.
      </p>
      <div className="task-form__actions">
        <button type="button" className="btn btn--ghost" onClick={onCancel} disabled={isDeleting}>
          Annuler
        </button>
        <button type="button" className="btn btn--danger" onClick={onConfirm} disabled={isDeleting}>
          {isDeleting ? "Suppression…" : "Supprimer"}
        </button>
      </div>
    </Modal>
  );
}
