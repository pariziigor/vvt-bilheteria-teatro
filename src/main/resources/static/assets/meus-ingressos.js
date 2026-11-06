const API = "/api/v1";
const TOKEN_KEY = "bilheteria.token";
const EMAIL_KEY = "bilheteria.email";
const ROLE_KEY = "bilheteria.papel";
const ticketState = document.querySelector("#ticket-state");
const ticketGrid = document.querySelector("#ticket-grid");
const notice = document.querySelector("#ticket-notice");
const navLogin = document.querySelector("#nav-login");
const navUser = document.querySelector("#nav-user");

function token() {
  return sessionStorage.getItem(TOKEN_KEY);
}

function isAdmin() {
  return sessionStorage.getItem(ROLE_KEY) === "ADMIN";
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (char) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char],
  );
}

function formatDate(value) {
  if (!value) return "Data não informada";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "long" }).format(
    new Date(`${value}T00:00:00`),
  );
}

function formatMoney(value) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(
    Number(value || 0),
  );
}

function showNotice(message, success = false) {
  notice.textContent = message;
  notice.classList.toggle("success", success);
  notice.classList.remove("hidden");
}

function updateNavigation() {
  const loggedIn = Boolean(token());
  navLogin.classList.toggle("hidden", loggedIn);
  navUser.classList.toggle("hidden", !loggedIn);
  if (!loggedIn) {
    navLogin.href = `/login.html?next=${encodeURIComponent("/meus-ingressos.html")}`;
  }
}

async function request(path, options = {}) {
  const headers = { "Content-Type": "application/json" };
  if (token()) headers.Authorization = `Bearer ${token()}`;
  const response = await fetch(`${API}${path}`, { ...options, headers });
  if (response.status === 401) {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(EMAIL_KEY);
    sessionStorage.removeItem(ROLE_KEY);
    updateNavigation();
    throw new Error("Sua sessão expirou. Entre novamente para continuar.");
  }
  if (!response.ok) {
    let message = "";
    try {
      message = (await response.json()).mensagem || "";
    } catch {
      message = "";
    }
    throw new Error(message || "Não foi possível carregar seus ingressos.");
  }
  return response.status === 204 ? null : response.json();
}

function renderTickets(sessions, plays) {
  const tickets = sessions.flatMap((session) =>
    (session.ingressos || []).map((ticket) => ({ session, ticket })),
  );
  tickets.sort((first, second) => {
    const a = `${first.session.dataHora?.data || ""}T${first.session.dataHora?.horaInicio || ""}`;
    const b = `${second.session.dataHora?.data || ""}T${second.session.dataHora?.horaInicio || ""}`;
    return a.localeCompare(b);
  });
  ticketGrid.replaceChildren();
  if (!tickets.length) {
    ticketState.innerHTML = '<div class="empty-mark" aria-hidden="true">✳</div><p>Você ainda não tem ingressos. <a href="/">Explore a programação</a> para escolher uma peça.</p>';
    ticketState.classList.remove("hidden");
    ticketGrid.classList.add("hidden");
    return;
  }

  tickets.forEach(({ session, ticket }) => {
    const play = plays.find((item) => item.id === session.pecaId);
    const active = ticket.status === "VENDIDO";
    const card = document.createElement("article");
    card.className = "ticket-card";
    card.innerHTML = `
      <div class="ticket-card-top">
        <span class="eyebrow eyebrow-dark">${escapeHtml(ticket.tipoIngresso || "Ingresso")}</span>
        <span class="ticket-status ${active ? "ticket-active" : "ticket-cancelled"}">${active ? "Confirmado" : "Cancelado"}</span>
      </div>
      <h3>${escapeHtml(play?.titulo || "Peça indisponível")}</h3>
      <p class="ticket-session-date">${escapeHtml(formatDate(session.dataHora?.data))} · ${escapeHtml(session.dataHora?.horaInicio || "--:--")}–${escapeHtml(session.dataHora?.horaFim || "--:--")}</p>
      <div class="ticket-card-footer">
        <strong>${formatMoney(ticket.valor)}</strong>
        ${active ? `<button class="cancel-ticket" type="button" data-session-id="${escapeHtml(session.id)}" data-ticket-id="${escapeHtml(ticket.id)}">Cancelar ingresso</button>` : ""}
      </div>`;
    ticketGrid.append(card);
  });
  ticketState.classList.add("hidden");
  ticketGrid.classList.remove("hidden");
}

async function loadTickets() {
  if (!token()) {
    ticketState.innerHTML = '<div class="empty-mark" aria-hidden="true">✳</div><p>Entre na sua conta para consultar seus ingressos.</p>';
    return;
  }
  if (isAdmin()) {
    ticketState.innerHTML = '<p>Esta área é destinada a contas de cliente. <a href="/">Volte à programação</a>.</p>';
    return;
  }
  try {
    const [sessions, plays] = await Promise.all([request("/sessoes"), request("/pecas")]);
    renderTickets(sessions, plays);
    if (new URLSearchParams(location.search).has("compra")) {
      showNotice("Compra concluída. Seu ingresso está salvo nesta área.", true);
    }
  } catch (error) {
    ticketState.innerHTML = `<div class="empty-mark" aria-hidden="true">✳</div><p>${escapeHtml(error.message)}</p>`;
    ticketState.classList.remove("hidden");
    ticketGrid.classList.add("hidden");
  }
}

navUser.addEventListener("click", () => {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(EMAIL_KEY);
  sessionStorage.removeItem(ROLE_KEY);
  updateNavigation();
  location.assign("/");
});

ticketGrid.addEventListener("click", async (event) => {
  const button = event.target.closest(".cancel-ticket");
  if (!button || !window.confirm("Deseja cancelar este ingresso?")) return;
  button.disabled = true;
  try {
    await request(
      `/sessoes/${encodeURIComponent(button.dataset.sessionId)}/ingressos/${encodeURIComponent(button.dataset.ticketId)}`,
      { method: "DELETE" },
    );
    showNotice("Ingresso cancelado. A vaga voltou a ficar disponível.", true);
    await loadTickets();
  } catch (error) {
    showNotice(error.message);
    button.disabled = false;
  }
});

updateNavigation();
loadTickets();
