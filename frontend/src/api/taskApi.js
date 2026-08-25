const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api/v1";

/**
 * Erreur métier normalisée, construite à partir du format ErrorResponseDTO
 * renvoyé par le GlobalExceptionHandler du backend.
 */
export class ApiError extends Error {
  constructor({ message, status, validationErrors }) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.validationErrors = validationErrors || null;
  }
}

async function request(path, options = {}) {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  // 204 No Content (ex: suppression) : rien à parser
  if (response.status === 204) {
    return null;
  }

  const isJson = response.headers.get("content-type")?.includes("application/json");
  const body = isJson ? await response.json() : null;

  if (!response.ok) {
    throw new ApiError({
      message: body?.message || "Une erreur est survenue lors de la communication avec le serveur.",
      status: response.status,
      validationErrors: body?.validationErrors,
    });
  }

  return body;
}

export const taskApi = {
  getAll: (status) => request(status ? `/tasks?status=${encodeURIComponent(status)}` : "/tasks"),

  getById: (id) => request(`/tasks/${id}`),

  create: (payload) =>
    request("/tasks", {
      method: "POST",
      body: JSON.stringify(payload),
    }),

  update: (id, payload) =>
    request(`/tasks/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    }),

  remove: (id) =>
    request(`/tasks/${id}`, {
      method: "DELETE",
    }),
};
