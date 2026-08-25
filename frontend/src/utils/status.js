export const STATUS_META = {
  TODO: { label: "À faire", short: "TODO", tone: "todo" },
  IN_PROGRESS: { label: "En cours", short: "EN COURS", tone: "progress" },
  DONE: { label: "Terminée", short: "TERMINÉE", tone: "done" },
  CANCELLED: { label: "Annulée", short: "ANNULÉE", tone: "cancelled" },
};

export const STATUS_ORDER = ["TODO", "IN_PROGRESS", "DONE", "CANCELLED"];

export function formatDate(isoString) {
  if (!isoString) return null;
  const date = new Date(isoString);
  if (Number.isNaN(date.getTime())) return null;
  return new Intl.DateTimeFormat("fr-FR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

export function ticketRef(id) {
  return `TCK-${String(id).padStart(4, "0")}`;
}
