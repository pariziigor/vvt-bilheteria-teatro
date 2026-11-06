const API = "/api/v1";
const TOKEN_KEY = "bilheteria.token";
const EMAIL_KEY = "bilheteria.email";
const ROLE_KEY = "bilheteria.papel";
const pageParameters = new URLSearchParams(window.location.search);
const pieceId = pageParameters.get("peca");
const title = document.querySelector("#play-title");
const description = document.querySelector("#play-description");
const playMeta = document.querySelector("#play-meta");
const playEyebrow = document.querySelector("#play-eyebrow");
const state = document.querySelector("#showtimes-state");
const grid = document.querySelector("#showtimes-grid");
const notice = document.querySelector("#showtimes-notice");
const scheduleTitle = document.querySelector("#showtimes-title");
const scheduleDescription = document.querySelector("#showtimes-description");
const adminToolbar = document.querySelector("#admin-session-toolbar");
const sessionForm = document.querySelector("#admin-session-form");
const sessionFormMessage = document.querySelector("#session-form-message");
let selectedPiece = null;
let selectedSessionId = pageParameters.get("sessao");
let sessionsCache = [];

function isLoggedIn() {
  return Boolean(sessionStorage.getItem(TOKEN_KEY));
}

function isAdmin() {
  return sessionStorage.getItem(ROLE_KEY) === "ADMIN";
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (character) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[character],
  );
}

function formatDate(value) {
  if (!value) return "Data não informada";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "long" }).format(
    new Date(`${value}T00:00:00`),
  );
}

function formatTime(value) {
  return value ? String(value).slice(0, 5) : "--:--";
}

function formatMoney(value) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(
    Number(value || 0),
  );
}

function ticketPrice(basePrice, category) {
  const discounts = { INTEIRA: 0, ESTUDANTE: 0.5, PCD: 0.6, IDOSO: 0.65 };
  return Number((Number(basePrice) * (1 - discounts[category])).toFixed(2));
}

function setNotice(message, success = false) {
  notice.textContent = message;
  notice.classList.toggle("success", success);
  notice.classList.remove("hidden");
}

function updateNavigation() {
  const loginLink = document.querySelector("#nav-login");
  const returnUrl = `${window.location.pathname}${window.location.search}`;
  loginLink.href = `/login.html?next=${encodeURIComponent(returnUrl)}`;
  loginLink.classList.toggle("hidden", isLoggedIn());
  document.querySelector("#nav-logout").classList.toggle("hidden", !isLoggedIn());
  document.querySelector("#nav-tickets").classList.toggle("hidden", !isLoggedIn() || isAdmin());
}

async function request(path, options = {}) {
  const headers = { "Content-Type": "application/json" };
  const savedToken = sessionStorage.getItem(TOKEN_KEY);
  if (savedToken) headers.Authorization = `Bearer ${savedToken}`;
  const response = await fetch(`${API}${path}`, { ...options, headers });
  if (response.status === 401 && savedToken) {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(EMAIL_KEY);
    sessionStorage.removeItem(ROLE_KEY);
    updateNavigation();
    throw new Error("Sua sessão expirou. Entre novamente para continuar.");
  }
  if (!response.ok) {
    let serverMessage = "";
    try {
      serverMessage = (await response.json()).mensagem || "";
    } catch {
      serverMessage = "";
    }
    const errors = {
      400: "Confira os dados informados e tente novamente.",
      401: "Entre na sua conta para continuar.",
      403: "Você não tem permissão para realizar esta ação.",
      404: "A peça ou sessão não foi encontrada.",
      409: "Não foi possível concluir. Confira a disponibilidade e os ingressos vendidos.",
    };
    throw new Error(serverMessage || errors[response.status] || "Não foi possível concluir a solicitação.");
  }
  return response.status === 204 ? null : response.json();
}

function renderPiece(piece) {
  selectedPiece = piece;
  title.textContent = piece.titulo;
  description.textContent = piece.descricao;
  playMeta.innerHTML = `<span>${escapeHtml(piece.classificacao)}</span><span>${Number(piece.duracaoMinutos)} min</span>`;
  document.title = `${piece.titulo} · Bilheteria do Teatro`;
  playEyebrow.textContent = isAdmin() ? "Gerencie esta peça" : "Escolha sua sessão";
  scheduleTitle.textContent = isAdmin() ? "Sessões desta peça" : "Horários disponíveis";
  scheduleDescription.textContent = isAdmin()
    ? "Gerencie datas, disponibilidade e vendas para esta peça."
    : "Selecione uma sessão para ver as opções de compra.";
  adminToolbar.classList.toggle("hidden", !isAdmin());
}

function renderSessions(allSessions) {
  sessionsCache = allSessions;
  if (isAdmin()) {
    renderAdminSessions(allSessions);
    return;
  }

  const sessions = allSessions
    .filter((session) => session.pecaId === selectedPiece.id)
    .filter((session) => new Date(`${session.dataHora?.data}T${session.dataHora?.horaInicio}`) > new Date())
    .sort(compareSessions);
  const selectedSession = sessions.find((session) => session.id === selectedSessionId);
  if (selectedSession) {
    renderSelectedSession(selectedSession);
    return;
  }
  selectedSessionId = null;
  window.history.replaceState({}, "", `/sessoes.html?peca=${encodeURIComponent(pieceId)}`);
  updateNavigation();
  scheduleTitle.textContent = "Horários disponíveis";
  scheduleDescription.textContent = "Selecione uma sessão para ver as opções de compra.";
  grid.replaceChildren();
  grid.classList.remove("purchase-mode");
  if (!sessions.length) {
    state.innerHTML = "<p>Não há sessões futuras disponíveis para esta peça no momento.</p>";
    state.classList.remove("hidden");
    grid.classList.add("hidden");
    return;
  }

  sessions.forEach((session) => grid.append(createCustomerSessionCard(session)));
  state.classList.add("hidden");
  grid.classList.remove("hidden");
}

function compareSessions(first, second) {
  const a = `${first.dataHora?.data || ""}T${first.dataHora?.horaInicio || ""}`;
  const b = `${second.dataHora?.data || ""}T${second.dataHora?.horaInicio || ""}`;
  return a.localeCompare(b);
}

function remainingSeats(session) {
  return Number(session.quantidadeIngressosDisponiveis ?? 0);
}

function availabilityMarkup(session) {
  const remaining = remainingSeats(session);
  return `<span class="availability ${remaining === 0 ? "sold-out" : ""}">${remaining === 0 ? "Esgotada" : `${remaining} ${remaining === 1 ? "vaga" : "vagas"}`}</span>`;
}

function createCustomerSessionCard(session) {
  const card = document.createElement("article");
  card.className = "session-card showtime-card";
  const remaining = remainingSeats(session);
  card.innerHTML = `
    <div class="session-card-head">
      <span class="play-number">${escapeHtml(formatDate(session.dataHora?.data))}</span>
      ${availabilityMarkup(session)}
    </div>
    <h3>${escapeHtml(formatTime(session.dataHora?.horaInicio))}–${escapeHtml(formatTime(session.dataHora?.horaFim))}</h3>
    <p class="showtime-price">Inteira · ${formatMoney(session.valorBaseIngresso)}</p>
    <button class="button button-outline button-full showtime-select" type="button" data-session-id="${escapeHtml(session.id)}" ${remaining === 0 ? "disabled" : ""}>
      ${remaining === 0 ? "Sessão esgotada" : "Selecionar sessão"}
    </button>`;
  return card;
}

function renderAdminSessions(allSessions) {
  selectedSessionId = null;
  const sessions = allSessions.filter((session) => session.pecaId === selectedPiece.id).sort(compareSessions);
  grid.replaceChildren();
  grid.classList.remove("purchase-mode");
  window.history.replaceState({}, "", `/sessoes.html?peca=${encodeURIComponent(pieceId)}`);
  updateNavigation();

  if (!sessions.length) {
    state.innerHTML = "<p>Esta peça ainda não tem sessões. Crie a primeira por aqui.</p>";
    state.classList.remove("hidden");
    grid.classList.add("hidden");
    return;
  }

  sessions.forEach((session) => grid.append(createAdminSessionCard(session)));
  state.classList.add("hidden");
  grid.classList.remove("hidden");
}

function createAdminSessionCard(session) {
  const card = document.createElement("article");
  card.className = "session-card admin-session-card";
  const tickets = session.ingressos || [];
  const sold = Number(session.quantidadeIngressosVendidos ?? tickets.filter((ticket) => ticket.status === "VENDIDO").length);
  const remaining = Number(session.quantidadeIngressosDisponiveis ?? Math.max(0, session.capacidade - sold));
  const sessionStarted = new Date(`${session.dataHora?.data}T${session.dataHora?.horaInicio}`) <= new Date();
  const ticketRows = tickets.map((ticket) => `
    <div class="ticket-row">
      <span><strong>${escapeHtml(ticket.tipoIngresso || "Ingresso")}</strong><small>${formatMoney(ticket.valor)} · ${ticket.status === "VENDIDO" ? "Vendido" : "Cancelado"}</small></span>
      ${ticket.status === "VENDIDO" && !sessionStarted ? `<button class="cancel-ticket" type="button" data-session-id="${escapeHtml(session.id)}" data-ticket-id="${escapeHtml(ticket.id)}">Cancelar</button>` : ""}
    </div>`).join("");

  card.innerHTML = `
    <div class="session-card-head">
      <span class="play-number">${escapeHtml(formatDate(session.dataHora?.data))}</span>
      ${availabilityMarkup(session)}
    </div>
    <h3>${escapeHtml(formatTime(session.dataHora?.horaInicio))}–${escapeHtml(formatTime(session.dataHora?.horaFim))}</h3>
    <div class="session-meta"><span>Inteira ${formatMoney(session.valorBaseIngresso)}</span><span>${sold}/${session.capacidade} vendidos</span></div>
    <div class="ticket-list">
      <p class="ticket-list-title">Ingressos da sessão · ${tickets.length}</p>
      ${ticketRows || '<p class="no-tickets">Nenhum ingresso vendido.</p>'}
    </div>
    <div class="session-actions">
      <button class="text-action edit-session" type="button" data-session-id="${escapeHtml(session.id)}">Editar</button>
      <button class="text-action danger remove-session" type="button" data-session-id="${escapeHtml(session.id)}">Remover sessão</button>
    </div>`;
  return card;
}

function renderSelectedSession(session) {
  selectedSessionId = session.id;
  scheduleTitle.textContent = "Comprar ingresso";
  scheduleDescription.textContent = "Confira os dados da sessão antes de continuar.";
  const remaining = remainingSeats(session);
  const tickets = session.ingressos || [];
  const purchaseControl = remaining === 0
    ? '<button class="button button-outline button-full" type="button" disabled>Sessão esgotada</button>'
    : isLoggedIn()
      ? `<form class="ticket-buy-form" data-session-id="${escapeHtml(session.id)}">
          <label class="field">Tipo de ingresso
            <select name="tipoIngresso" required>
              <option value="INTEIRA">Inteira · ${formatMoney(ticketPrice(session.valorBaseIngresso, "INTEIRA"))}</option>
              <option value="ESTUDANTE">Estudante · ${formatMoney(ticketPrice(session.valorBaseIngresso, "ESTUDANTE"))}</option>
              <option value="PCD">PCD · ${formatMoney(ticketPrice(session.valorBaseIngresso, "PCD"))}</option>
              <option value="IDOSO">Idoso · ${formatMoney(ticketPrice(session.valorBaseIngresso, "IDOSO"))}</option>
            </select>
          </label>
          <p class="ticket-price-preview" aria-live="polite">Valor total: ${formatMoney(ticketPrice(session.valorBaseIngresso, "INTEIRA"))}</p>
          <button class="button button-dark button-full" type="submit">Comprar ingresso</button>
        </form>`
      : `<a class="button button-dark button-full" href="/login.html?next=${encodeURIComponent(`/sessoes.html?peca=${pieceId}&sessao=${session.id}`)}">Entrar para comprar</a>`;
  const ticketList = isLoggedIn() && tickets.length
    ? `<div class="ticket-list">
        <p class="ticket-list-title">Seus ingressos nesta sessão</p>
        ${tickets.map((ticket) => `<div class="ticket-row">
          <span><strong>${escapeHtml(ticket.tipoIngresso || "Ingresso")}</strong><small>${formatMoney(ticket.valor)} · ${ticket.status === "VENDIDO" ? "Confirmado" : "Cancelado"}</small></span>
          ${ticket.status === "VENDIDO" ? `<button class="cancel-ticket showtime-cancel" type="button" data-session-id="${escapeHtml(session.id)}" data-ticket-id="${escapeHtml(ticket.id)}">Cancelar</button>` : ""}
        </div>`).join("")}
      </div>`
    : "";
  const card = document.createElement("article");
  card.className = "session-card showtime-card selected-showtime";
  const backButton = document.createElement("button");
  backButton.className = "text-action showtimes-back-button";
  backButton.type = "button";
  backButton.textContent = "← Voltar aos horários";
  card.innerHTML = `
    <div class="session-card-head">
      <span class="play-number">${escapeHtml(formatDate(session.dataHora?.data))}</span>
      ${availabilityMarkup(session)}
    </div>
    <h3>${escapeHtml(formatTime(session.dataHora?.horaInicio))}–${escapeHtml(formatTime(session.dataHora?.horaFim))}</h3>
    <p class="showtime-price">Inteira · ${formatMoney(session.valorBaseIngresso)}</p>
    ${purchaseControl}
    ${ticketList}`;
  grid.replaceChildren(backButton, card);
  grid.classList.add("purchase-mode");
  state.classList.add("hidden");
  grid.classList.remove("hidden");
}

function showSessionOptions() {
  selectedSessionId = null;
  window.history.replaceState({}, "", `/sessoes.html?peca=${encodeURIComponent(pieceId)}`);
  renderSessions(sessionsCache);
}

function openSessionForm(session = null) {
  sessionForm.reset();
  sessionForm.elements.sessaoId.value = session?.id || "";
  if (session) {
    sessionForm.elements.data.value = session.dataHora?.data || "";
    sessionForm.elements.horaInicio.value = formatTime(session.dataHora?.horaInicio);
    sessionForm.elements.horaFim.value = formatTime(session.dataHora?.horaFim);
    sessionForm.elements.capacidade.value = session.capacidade;
    sessionForm.elements.valorBaseIngresso.value = session.valorBaseIngresso;
  }
  document.querySelector("#session-form-eyebrow").textContent = session ? "Editar sessão" : "Nova sessão";
  document.querySelector("#session-form-title").textContent = session
    ? "Atualize os dados desta sessão"
    : `Adicione uma sessão para ${selectedPiece.titulo}`;
  document.querySelector("#session-submit").innerHTML = session
    ? "Salvar alterações"
    : 'Salvar sessão <span aria-hidden="true">↗</span>';
  sessionFormMessage.textContent = "";
  sessionForm.classList.remove("hidden");
  sessionForm.scrollIntoView({ behavior: "smooth", block: "center" });
}

function validateSession(data) {
  const now = new Date();
  const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
  if (data.data < today) return "A data da sessão não pode ser anterior a hoje.";
  if (data.horaFim <= data.horaInicio) return "O horário de término deve ser posterior ao horário de início.";
  if (data.data === today) {
    const current = `${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}`;
    if (data.horaInicio <= current) return "Para uma sessão hoje, o horário de início precisa ser futuro.";
  }
  return "";
}

async function loadPage() {
  updateNavigation();
  if (!pieceId) {
    title.textContent = "Peça não informada";
    state.innerHTML = '<p>Volte à <a href="/">programação</a> e escolha uma peça.</p>';
    return;
  }
  try {
    const [pieces, sessions] = await Promise.all([request("/pecas"), request("/sessoes")]);
    const piece = pieces.find((item) => item.id === pieceId);
    if (!piece) {
      title.textContent = "Peça não encontrada";
      state.innerHTML = '<p>Volte à <a href="/">programação</a> e escolha outra peça.</p>';
      state.classList.remove("hidden");
      return;
    }
    renderPiece(piece);
    renderSessions(sessions);
  } catch (error) {
    title.textContent = "Não foi possível carregar as sessões";
    state.innerHTML = `<p>${escapeHtml(error.message)}</p>`;
    state.classList.remove("hidden");
    grid.classList.add("hidden");
  }
}

grid.addEventListener("submit", async (event) => {
  if (!event.target.matches(".ticket-buy-form")) return;
  event.preventDefault();
  const buyForm = event.target;
  const button = buyForm.querySelector('button[type="submit"]');
  button.disabled = true;
  try {
    await request(`/sessoes/${encodeURIComponent(buyForm.dataset.sessionId)}/ingressos`, {
      method: "POST",
      body: JSON.stringify({ tipoIngresso: buyForm.elements.tipoIngresso.value }),
    });
    window.location.assign("/meus-ingressos.html?compra=sucesso");
  } catch (error) {
    setNotice(error.message);
    button.disabled = false;
  }
});

grid.addEventListener("change", (event) => {
  const select = event.target.closest('.ticket-buy-form select[name="tipoIngresso"]');
  if (!select) return;
  const session = sessionsCache.find((item) => item.id === select.closest(".ticket-buy-form").dataset.sessionId);
  const preview = select.closest(".ticket-buy-form").querySelector(".ticket-price-preview");
  if (session && preview) {
    preview.textContent = `Valor total: ${formatMoney(ticketPrice(session.valorBaseIngresso, select.value))}`;
  }
});

grid.addEventListener("click", async (event) => {
  const sessionButton = event.target.closest("button[data-session-id]");
  if (isAdmin() && sessionButton) {
    const sessionId = sessionButton.dataset.sessionId;
    const session = sessionsCache.find((item) => item.id === sessionId);
    try {
      if (sessionButton.classList.contains("edit-session")) {
        openSessionForm(session);
      } else if (sessionButton.classList.contains("remove-session")) {
        if (!window.confirm("Remover esta sessão? Sessões com ingressos vendidos não podem ser removidas.")) return;
        await request(`/sessoes/${encodeURIComponent(sessionId)}`, { method: "DELETE" });
        setNotice("Sessão removida.", true);
        await loadPage();
      } else if (sessionButton.classList.contains("cancel-ticket")) {
        if (!window.confirm("Deseja cancelar este ingresso?")) return;
        await request(`/sessoes/${encodeURIComponent(sessionId)}/ingressos/${encodeURIComponent(sessionButton.dataset.ticketId)}`, { method: "DELETE" });
        setNotice("Ingresso cancelado. A vaga voltou a ficar disponível.", true);
        await loadPage();
      }
    } catch (error) {
      setNotice(error.message);
    }
    return;
  }

  const selectButton = event.target.closest(".showtime-select[data-session-id]");
  if (selectButton) {
    const session = sessionsCache.find((item) => item.id === selectButton.dataset.sessionId);
    if (!session) return;
    selectedSessionId = session.id;
    window.history.replaceState({}, "", `/sessoes.html?peca=${encodeURIComponent(pieceId)}&sessao=${encodeURIComponent(session.id)}`);
    updateNavigation();
    renderSelectedSession(session);
    return;
  }

  if (event.target.closest(".showtimes-back-button")) {
    showSessionOptions();
    updateNavigation();
    return;
  }

  const cancelButton = event.target.closest(".showtime-cancel[data-session-id]");
  if (!cancelButton || !window.confirm("Deseja cancelar este ingresso?")) return;
  cancelButton.disabled = true;
  try {
    await request(`/sessoes/${encodeURIComponent(cancelButton.dataset.sessionId)}/ingressos/${encodeURIComponent(cancelButton.dataset.ticketId)}`, { method: "DELETE" });
    setNotice("Ingresso cancelado. A vaga voltou a ficar disponível.", true);
    await loadPage();
  } catch (error) {
    setNotice(error.message);
    cancelButton.disabled = false;
  }
});

sessionForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(sessionForm));
  const id = data.sessaoId;
  const body = {
    pecaId: selectedPiece.id,
    data: data.data,
    horaInicio: data.horaInicio,
    horaFim: data.horaFim,
    capacidade: Number(data.capacidade),
    valorBaseIngresso: Number(data.valorBaseIngresso),
  };
  sessionFormMessage.textContent = validateSession(body);
  if (sessionFormMessage.textContent) return;

  const submit = document.querySelector("#session-submit");
  submit.disabled = true;
  try {
    await request(id ? `/sessoes/${encodeURIComponent(id)}` : "/sessoes", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(body),
    });
    sessionForm.reset();
    sessionForm.classList.add("hidden");
    setNotice(id ? "Sessão atualizada." : "Sessão criada.", true);
    await loadPage();
  } catch (error) {
    sessionFormMessage.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

document.querySelector("#add-session-toggle").addEventListener("click", () => openSessionForm());
document.querySelector("#close-session-form").addEventListener("click", () => sessionForm.classList.add("hidden"));
sessionForm.elements.data.addEventListener("change", () => { sessionFormMessage.textContent = ""; });
sessionForm.elements.horaInicio.addEventListener("input", () => { sessionFormMessage.textContent = ""; });
sessionForm.elements.horaFim.addEventListener("input", () => { sessionFormMessage.textContent = ""; });

document.querySelector("#nav-logout").addEventListener("click", () => {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(EMAIL_KEY);
  sessionStorage.removeItem(ROLE_KEY);
  updateNavigation();
  loadPage();
});

loadPage();
