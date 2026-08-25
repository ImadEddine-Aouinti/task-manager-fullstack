import { STATUS_META, STATUS_ORDER } from "../utils/status";

export default function StatusFilter({ value, onChange, counts }) {
  const tabs = [{ key: null, label: "Toutes" }, ...STATUS_ORDER.map((s) => ({ key: s, label: STATUS_META[s].label }))];

  return (
    <div className="status-filter" role="tablist" aria-label="Filtrer les tâches par statut">
      {tabs.map((tab) => {
        const isActive = value === tab.key;
        return (
          <button
            key={tab.key ?? "all"}
            role="tab"
            aria-selected={isActive}
            className={`status-filter__tab ${isActive ? "is-active" : ""} ${
              tab.key ? `tone-${STATUS_META[tab.key].tone}` : ""
            }`}
            onClick={() => onChange(tab.key)}
          >
            <span className="status-filter__dot" aria-hidden="true" />
            {tab.label}
            {typeof counts[tab.key ?? "all"] === "number" && (
              <span className="status-filter__count">{counts[tab.key ?? "all"]}</span>
            )}
          </button>
        );
      })}
    </div>
  );
}
