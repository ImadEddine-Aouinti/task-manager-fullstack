import { useState } from "react";
import { STATUS_META, STATUS_ORDER } from "../utils/status";
import { ApiError } from "../api/taskApi";

const EMPTY_FORM = { title: "", description: "", status: "TODO", dueDate: "" };

function toDatetimeLocal(isoString) {
  if (!isoString) return "";
  return isoString.slice(0, 16); // "yyyy-MM-ddTHH:mm"
}

export default function TaskForm({ initialTask, onSubmit, onCancel }) {
  const [form, setForm] = useState(
    initialTask
      ? {
          title: initialTask.title ?? "",
          description: initialTask.description ?? "",
          status: initialTask.status ?? "TODO",
          dueDate: toDatetimeLocal(initialTask.dueDate),
        }
      : EMPTY_FORM
  );
  const [fieldErrors, setFieldErrors] = useState({});
  const [globalError, setGlobalError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validateClientSide() {
    const errors = {};
    if (!form.title.trim()) {
      errors.title = "Le titre est obligatoire.";
    } else if (form.title.length > 150) {
      errors.title = "Le titre ne doit pas dépasser 150 caractères.";
    }
    if (form.description.length > 1000) {
      errors.description = "La description ne doit pas dépasser 1000 caractères.";
    }
    return errors;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setGlobalError(null);

    const clientErrors = validateClientSide();
    if (Object.keys(clientErrors).length > 0) {
      setFieldErrors(clientErrors);
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit({
        title: form.title.trim(),
        description: form.description.trim() || null,
        status: form.status,
        dueDate: form.dueDate ? new Date(form.dueDate).toISOString() : null,
      });
    } catch (err) {
      if (err instanceof ApiError && err.validationErrors) {
        setFieldErrors(err.validationErrors);
      } else {
        setGlobalError(err.message || "Une erreur est survenue, réessayez.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  function updateField(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
    setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
  }

  return (
    <form className="task-form" onSubmit={handleSubmit} noValidate>
      {globalError && <p className="form-error form-error--global">{globalError}</p>}

      <label className="field">
        <span className="field__label">Titre *</span>
        <input
          type="text"
          value={form.title}
          onChange={(e) => updateField("title", e.target.value)}
          maxLength={150}
          autoFocus
        />
        {fieldErrors.title && <span className="field__error">{fieldErrors.title}</span>}
      </label>

      <label className="field">
        <span className="field__label">Description</span>
        <textarea
          value={form.description}
          onChange={(e) => updateField("description", e.target.value)}
          maxLength={1000}
          rows={4}
        />
        {fieldErrors.description && <span className="field__error">{fieldErrors.description}</span>}
      </label>

      <div className="field-row">
        <label className="field">
          <span className="field__label">Statut</span>
          <select value={form.status} onChange={(e) => updateField("status", e.target.value)}>
            {STATUS_ORDER.map((status) => (
              <option key={status} value={status}>
                {STATUS_META[status].label}
              </option>
            ))}
          </select>
        </label>

        <label className="field">
          <span className="field__label">Échéance</span>
          <input
            type="datetime-local"
            value={form.dueDate}
            onChange={(e) => updateField("dueDate", e.target.value)}
          />
          {fieldErrors.dueDate && <span className="field__error">{fieldErrors.dueDate}</span>}
        </label>
      </div>

      <div className="task-form__actions">
        <button type="button" className="btn btn--ghost" onClick={onCancel} disabled={isSubmitting}>
          Annuler
        </button>
        <button type="submit" className="btn btn--primary" disabled={isSubmitting}>
          {isSubmitting ? "Enregistrement…" : initialTask ? "Enregistrer" : "Créer la tâche"}
        </button>
      </div>
    </form>
  );
}
