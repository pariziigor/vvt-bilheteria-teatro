const API = "/api/v1";
const TOKEN_KEY = "bilheteria.token";
const EMAIL_KEY = "bilheteria.email";
const ROLE_KEY = "bilheteria.papel";
const form = document.querySelector("#auth-form");
const notice = document.querySelector("#notice");
const authPanel = document.querySelector("#auth-panel");
const catalogToolbar = document.querySelector("#catalog-toolbar");
const catalogState = document.querySelector("#catalog-state");
const playGrid = document.querySelector("#play-grid");
const playForm = document.querySelector("#play-form");
const sessionForm = document.querySelector("#session-form");
const sessionGrid = document.querySelector("#session-grid");
const sessionState = document.querySelector("#session-state");
const sessionToolbar = document.querySelector("#session-toolbar");
const sessionNotice = document.querySelector("#session-notice");
let authMode = "login";
let playsCache = [];

function token() {
  return sessionStorage.getItem(TOKEN_KEY);
}
function isLoggedIn() {
  return Boolean(token());
}
function isAdmin() {
  return sessionStorage.getItem(ROLE_KEY) === "ADMIN";
}

function showNotice(message, success = false) {
  notice.textContent = message;
  notice.classList.toggle("success", success);
  notice.classList.remove("hidden");
  window.setTimeout(() => notice.classList.add("hidden"), 5000);
}

async function request(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  if (token()) headers.Authorization = `Bearer ${token()}`;
  const response = await fetch(`${API}${path}`, { ...options, headers });
  if (response.status === 401) {
    if (token()) {
      signOut(false);
      throw new Error("Sua sessão expirou. Entre novamente para continuar.");
    }
    if (path === "/autenticacao") {
      throw new Error("E-mail ou senha incorretos.");
    }
    if (path === "/pecas" || path === "/sessoes" || path.startsWith("/sessoes/")) {
      throw new Error(
        "O servidor não liberou a programação pública. Reinicie a aplicação e atualize a página.",
      );
    }
    throw new Error("Entre na sua conta para continuar.");
  }
  if (!response.ok) {
    const messages = {
      400: "Confira os dados informados e tente novamente.",
      403: "Você não tem permissão para realizar esta ação.",
      404: "O item solicitado não foi encontrado.",
      409: "A operação não pode ser concluída: confira a disponibilidade e os ingressos vendidos.",
    };
    if (response.status === 409 && path === "/usuarios") {
      throw new Error("Este e-mail já possui uma conta cadastrada.");
    }
    throw new Error(
      messages[response.status] || "Não foi possível concluir a solicitação. Tente novamente.",
    );
  }
  return response.status === 204 ? null : response.json();
}

function setAuthMode(mode) {
  authMode = mode;
  const registering = mode === "register";
  document.querySelector("#login-tab").classList.toggle("active", !registering);
  document.querySelector("#register-tab").classList.toggle("active", registering);
  document.querySelector("#login-tab").setAttribute("aria-selected", String(!registering));
  document.querySelector("#register-tab").setAttribute("aria-selected", String(registering));
  document.querySelector("#name-field").classList.toggle("hidden", !registering);
  document.querySelector("[name=nome]").required = registering;
  document.querySelector("[name=senha]").autocomplete = registering
    ? "new-password"
    : "current-password";
  document.querySelector("#auth-submit").innerHTML = registering
    ? 'Criar minha conta <span aria-hidden="true">↗</span>'
    : 'Entrar na bilheteria <span aria-hidden="true">↗</span>';
  document.querySelector("#auth-message").textContent = "";
}

function renderSignedIn() {
  authPanel.classList.add("hidden");
  catalogToolbar.classList.remove("hidden");
  sessionToolbar.classList.toggle("hidden", !isAdmin());
  document.querySelector("#add-play-toggle").classList.toggle("hidden", !isAdmin());
  document.querySelector("#add-session-toggle").classList.toggle("hidden", !isAdmin());
  document.querySelector("#sessions-description").innerHTML = isAdmin()
    ? "Gerencie a programação e acompanhe as vendas das sessões."
    : "Escolha uma sessão, consulte a disponibilidade<br>e garanta seu lugar.";
  document.querySelector("#nav-auth").classList.add("hidden");
  document.querySelector("#nav-user").classList.remove("hidden");
  const email = sessionStorage.getItem(EMAIL_KEY) || "";
  const roleLabel = isAdmin() ? "Admin" : "Cliente";
  document.querySelector("#welcome-message").textContent = email
    ? `Olá, ${email}. Perfil: ${roleLabel}.`
    : `Perfil: ${roleLabel}.`;
  loadPlays().then(loadSessions);
}

function signOut(showMessage = true) {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(EMAIL_KEY);
  sessionStorage.removeItem(ROLE_KEY);
  authPanel.classList.remove("hidden");
  catalogToolbar.classList.add("hidden");
  playForm.classList.add("hidden");
  sessionForm.classList.add("hidden");
  sessionToolbar.classList.add("hidden");
  sessionGrid.classList.add("hidden");
  sessionState.classList.remove("hidden");
  sessionState.innerHTML =
    '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando sessões…</p>';
  playGrid.classList.add("hidden");
  catalogState.classList.remove("hidden");
  catalogState.innerHTML =
    '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando a programação…</p>';
  document.querySelector("#nav-auth").classList.remove("hidden");
  document.querySelector("#nav-user").classList.add("hidden");
  if (showMessage) showNotice("Você saiu da sua conta.", true);
  loadPlays().then(loadSessions);
}

async function loadPlays() {
  catalogState.classList.remove("hidden");
  catalogState.innerHTML =
    '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando a programação…</p>';
  playGrid.classList.add("hidden");
  try {
    const plays = await request("/pecas");
    playsCache = plays;
    updatePlaySelect();
    renderPlays(plays);
  } catch (error) {
    playsCache = [];
    updatePlaySelect();
    catalogState.innerHTML = `<div class="empty-mark" aria-hidden="true">✳</div><p>${escapeHtml(error.message)}</p>`;
  }
}

function updatePlaySelect(selected = "") {
  const select = sessionForm.elements.pecaId;
  select.replaceChildren(new Option("Selecione uma peça", ""));
  playsCache.forEach((play) => select.add(new Option(play.titulo, play.id)));
  select.value = selected;
  select.disabled = playsCache.length === 0;
}

function formatDate(date) {
  if (!date) return "Data não informada";
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "long" }).format(
    new Date(`${date}T00:00:00`),
  );
}

function formatMoney(value) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(
    Number(value || 0),
  );
}

function showSessionNotice(message, success = false) {
  sessionNotice.textContent = message;
  sessionNotice.classList.toggle("success", success);
  sessionNotice.classList.remove("hidden");
  window.setTimeout(() => sessionNotice.classList.add("hidden"), 5000);
}

async function loadSessions() {
  sessionState.classList.remove("hidden");
  sessionState.innerHTML =
    '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando sessões…</p>';
  sessionGrid.classList.add("hidden");
  try {
    const sessions = await request("/sessoes");
    renderSessions(sessions);
  } catch (error) {
    sessionState.innerHTML = `<div class="empty-mark" aria-hidden="true">✳</div><p>${escapeHtml(error.message)}</p>`;
  }
}

function renderSessions(sessions) {
  sessionGrid.replaceChildren();
  if (!sessions.length) {
    sessionState.innerHTML =
      '<div class="empty-mark" aria-hidden="true">✳</div><p>Ainda não há sessões cadastradas.</p>';
    sessionState.classList.remove("hidden");
    sessionGrid.classList.add("hidden");
    return;
  }
  sessions.forEach((session) => {
    const play = playsCache.find((item) => item.id === session.pecaId);
    const tickets = session.ingressos || [];
    const remaining = Number(
      session.quantidadeIngressosDisponiveis ??
        session.capacidade - tickets.filter((ticket) => ticket.status === "VENDIDO").length,
    );
    const administrador = isAdmin();
    const purchaseForm = administrador
      ? ""
      : isLoggedIn()
        ? `
      <form class="ticket-buy-form" data-session-id="${escapeHtml(session.id)}">
        <label class="field">Tipo de ingresso<select name="tipoIngresso" required><option value="INTEIRA">Inteira</option><option value="ESTUDANTE">Estudante</option><option value="PCD">PCD</option><option value="IDOSO">Idoso</option></select></label>
        <button class="button button-dark button-full" type="submit" ${remaining === 0 ? "disabled" : ""}>Comprar ingresso <span aria-hidden="true">↗</span></button>
      </form>`
        : `<a class="button button-outline button-full login-to-buy" href="#acesso">Entre para comprar</a>`;
    const ticketEmptyText = administrador
      ? "Nenhum ingresso vendido ainda."
      : isLoggedIn()
        ? "Você ainda não comprou ingressos para esta sessão."
        : "Entre para consultar e gerenciar seus ingressos.";
    const sessionActions = administrador
      ? `<button class="text-action session-details" type="button" data-session-id="${escapeHtml(session.id)}">Recarregar</button><button class="text-action edit-session" type="button" data-session-id="${escapeHtml(session.id)}">Editar</button><button class="text-action danger remove-session" type="button" data-session-id="${escapeHtml(session.id)}">Remover sessão</button>`
      : isLoggedIn()
        ? `<button class="text-action session-details" type="button" data-session-id="${escapeHtml(session.id)}">Recarregar detalhes</button>`
        : "";
    const card = document.createElement("article");
    card.className = "session-card";
    card.innerHTML = `
      <div class="session-card-head"><span class="play-number">${escapeHtml(formatDate(session.dataHora?.data))}</span><span class="availability ${remaining === 0 ? "sold-out" : ""}">${remaining} ${remaining === 1 ? "vaga" : "vagas"}</span></div>
      <h3>${escapeHtml(play?.titulo || "Peça indisponível")}</h3>
      <p class="session-time">${escapeHtml(session.dataHora?.horaInicio || "--:--")}–${escapeHtml(session.dataHora?.horaFim || "--:--")}</p>
      <div class="session-meta"><span>Inteira ${formatMoney(session.valorBaseIngresso)}</span><span>${tickets.filter((ticket) => ticket.status === "VENDIDO").length}/${session.capacidade} vendidos</span></div>
      ${purchaseForm}
      <div class="ticket-list"><p class="ticket-list-title">Ingressos desta sessão</p>
        ${tickets.length ? tickets.map((ticket) => `<div class="ticket-row"><span><strong>${escapeHtml(ticket.tipoIngresso?.categoria || ticket.tipoIngresso || "Ingresso")}</strong><small>${formatMoney(ticket.valor)} · ${escapeHtml(ticket.status)}</small></span>${ticket.status === "VENDIDO" ? `<button class="cancel-ticket" type="button" data-session-id="${escapeHtml(session.id)}" data-ticket-id="${escapeHtml(ticket.id)}">Cancelar</button>` : ""}</div>`).join("") : `<p class="no-tickets">${ticketEmptyText}</p>`}
      </div>
      <div class="session-actions">${sessionActions}</div>`;
    sessionGrid.append(card);
  });
  sessionState.classList.add("hidden");
  sessionGrid.classList.remove("hidden");
}

function openSessionForm(session = null) {
  sessionForm.reset();
  sessionForm.elements.sessaoId.value = session?.id || "";
  updatePlaySelect(session?.pecaId || "");
  if (session) {
    sessionForm.elements.data.value = session.dataHora?.data || "";
    sessionForm.elements.horaInicio.value = session.dataHora?.horaInicio || "";
    sessionForm.elements.horaFim.value = session.dataHora?.horaFim || "";
    sessionForm.elements.capacidade.value = session.capacidade;
    sessionForm.elements.valorBaseIngresso.value = session.valorBaseIngresso;
  }
  document.querySelector("#session-form-eyebrow").textContent = session
    ? "Editar sessão"
    : "Nova sessão";
  document.querySelector("#session-form-title").textContent = session
    ? "Atualize os dados da sessão"
    : "Coloque uma sessão na agenda";
  document.querySelector("#session-submit").innerHTML = session
    ? 'Salvar alterações <span aria-hidden="true">↗</span>'
    : 'Salvar sessão <span aria-hidden="true">↗</span>';
  document.querySelector("#session-message").textContent = playsCache.length
    ? ""
    : "Cadastre uma peça antes de criar uma sessão.";
  sessionForm.classList.remove("hidden");
  sessionForm.scrollIntoView({ behavior: "smooth", block: "center" });
}

function validateSessionSchedule(data) {
  const today = new Date();
  const todayValue = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`;
  if (data.data < todayValue) {
    return {
      message: "A data da sessão não pode ser anterior a hoje.",
      field: sessionForm.elements.data,
    };
  }
  if (data.horaFim <= data.horaInicio) {
    return {
      message: "O horário de término deve ser posterior ao horário de início.",
      field: sessionForm.elements.horaFim,
    };
  }
  if (data.data === todayValue) {
    const currentTime = `${String(today.getHours()).padStart(2, "0")}:${String(today.getMinutes()).padStart(2, "0")}`;
    if (data.horaInicio <= currentTime) {
      return {
        message: "Para uma sessão hoje, o horário de início precisa ser futuro.",
        field: sessionForm.elements.horaInicio,
      };
    }
  }
  return null;
}

function updateScheduleMessage() {
  const data = {
    data: sessionForm.elements.data.value,
    horaInicio: sessionForm.elements.horaInicio.value,
    horaFim: sessionForm.elements.horaFim.value,
  };
  const message = document.querySelector("#session-message");
  if (!data.data || !data.horaInicio || !data.horaFim) {
    message.textContent = "";
    return;
  }
  message.textContent = validateSessionSchedule(data)?.message || "";
}

async function fetchSession(id) {
  return request(`/sessoes/${encodeURIComponent(id)}`);
}

async function handleSessionAction(event) {
  const button = event.target.closest("button[data-session-id]");
  if (!button) return;
  const id = button.dataset.sessionId;
  try {
    if (button.classList.contains("edit-session")) {
      openSessionForm(await fetchSession(id));
    } else if (button.classList.contains("session-details")) {
      await fetchSession(id);
      showSessionNotice("Os detalhes da sessão foram atualizados.", true);
      await loadSessions();
    } else if (button.classList.contains("remove-session")) {
      if (
        !window.confirm(
          "Remover esta sessão? Sessões com ingressos vendidos não podem ser removidas.",
        )
      )
        return;
      await request(`/sessoes/${encodeURIComponent(id)}`, { method: "DELETE" });
      showSessionNotice("Sessão removida.", true);
      await loadSessions();
    } else if (button.classList.contains("cancel-ticket")) {
      if (!window.confirm("Deseja cancelar este ingresso?")) return;
      await request(
        `/sessoes/${encodeURIComponent(id)}/ingressos/${encodeURIComponent(button.dataset.ticketId)}`,
        { method: "DELETE" },
      );
      showSessionNotice("Ingresso cancelado. A vaga voltou a ficar disponível.", true);
      await loadSessions();
    }
  } catch (error) {
    showSessionNotice(error.message);
  }
}

function renderPlays(plays) {
  playGrid.replaceChildren();
  if (!plays.length) {
    catalogState.innerHTML =
      '<div class="empty-mark" aria-hidden="true">✳</div><p>Ainda não há peças cadastradas. Que tal incluir a primeira?</p>';
    catalogState.classList.remove("hidden");
    playGrid.classList.add("hidden");
    return;
  }
  plays.forEach((play, index) => {
    const card = document.createElement("article");
    card.className = "play-card";
    card.innerHTML = `<span class="play-number">Em cena · ${String(index + 1).padStart(2, "0")}</span><h3>${escapeHtml(play.titulo)}</h3><p>${escapeHtml(play.descricao)}</p><div class="play-meta"><span>${escapeHtml(play.classificacao)}</span><span>${Number(play.duracaoMinutos)} min</span></div>`;
    playGrid.append(card);
  });
  catalogState.classList.add("hidden");
  playGrid.classList.remove("hidden");
}

function escapeHtml(value) {
  return String(value ?? "").replace(
    /[&<>"']/g,
    (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char],
  );
}

document.querySelector("#login-tab").addEventListener("click", () => setAuthMode("login"));
document.querySelector("#register-tab").addEventListener("click", () => setAuthMode("register"));
document
  .querySelector("#nav-auth")
  .addEventListener("click", () =>
    document.querySelector("#acesso").scrollIntoView({ behavior: "smooth" }),
  );
document.querySelector("#add-session-toggle").addEventListener("click", () => openSessionForm());
document
  .querySelector("#close-session-form")
  .addEventListener("click", () => sessionForm.classList.add("hidden"));
sessionForm.elements.data.addEventListener("change", updateScheduleMessage);
sessionForm.elements.horaInicio.addEventListener("input", updateScheduleMessage);
sessionForm.elements.horaFim.addEventListener("input", updateScheduleMessage);
sessionGrid.addEventListener("click", handleSessionAction);
sessionGrid.addEventListener("submit", async (event) => {
  if (!event.target.matches(".ticket-buy-form")) return;
  event.preventDefault();
  const formElement = event.target;
  const submit = formElement.querySelector("button[type=submit]");
  submit.disabled = true;
  try {
    await request(`/sessoes/${encodeURIComponent(formElement.dataset.sessionId)}/ingressos`, {
      method: "POST",
      body: JSON.stringify({ tipoIngresso: formElement.elements.tipoIngresso.value }),
    });
    showSessionNotice("Ingresso comprado com sucesso.", true);
    await loadSessions();
  } catch (error) {
    showSessionNotice(error.message);
    submit.disabled = false;
  }
});
document.querySelector("#nav-user").addEventListener("click", () => signOut());
document.querySelector("#add-play-toggle").addEventListener("click", () => {
  playForm.classList.toggle("hidden");
  if (!playForm.classList.contains("hidden"))
    playForm.scrollIntoView({ behavior: "smooth", block: "center" });
});
document
  .querySelector("#close-play-form")
  .addEventListener("click", () => playForm.classList.add("hidden"));

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const message = document.querySelector("#auth-message");
  const submit = document.querySelector("#auth-submit");
  const data = Object.fromEntries(new FormData(form));
  message.textContent = "";
  if (authMode === "register" && !data.nome.trim()) {
    message.textContent = "Informe seu nome para criar a conta.";
    return;
  }
  submit.disabled = true;
  try {
    if (authMode === "register") {
      await request("/usuarios", { method: "POST", body: JSON.stringify(data) });
      setAuthMode("login");
      document.querySelector("[name=email]").value = data.email;
      showNotice("Conta criada. Entre com seu e-mail e senha para continuar.", true);
    } else {
      const result = await request("/autenticacao", {
        method: "POST",
        body: JSON.stringify({ email: data.email, senha: data.senha }),
      });
      sessionStorage.setItem(TOKEN_KEY, result.token);
      sessionStorage.setItem(EMAIL_KEY, data.email);
      sessionStorage.setItem(ROLE_KEY, result.papel || "CLIENTE");
      form.reset();
      renderSignedIn();
    }
  } catch (error) {
    message.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

playForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const message = document.querySelector("#play-message");
  const submit = playForm.querySelector("button[type=submit]");
  const data = Object.fromEntries(new FormData(playForm));
  message.textContent = "";
  submit.disabled = true;
  try {
    await request("/pecas", {
      method: "POST",
      body: JSON.stringify({ ...data, duracaoMinutos: Number(data.duracaoMinutos) }),
    });
    playForm.reset();
    playForm.classList.add("hidden");
    showNotice("Peça cadastrada e adicionada à programação.", true);
    await loadPlays();
    await loadSessions();
  } catch (error) {
    message.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

sessionForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const message = document.querySelector("#session-message");
  const submit = document.querySelector("#session-submit");
  const data = Object.fromEntries(new FormData(sessionForm));
  const id = data.sessaoId;
  const body = {
    pecaId: data.pecaId,
    data: data.data,
    horaInicio: data.horaInicio,
    horaFim: data.horaFim,
    capacidade: Number(data.capacidade),
    valorBaseIngresso: Number(data.valorBaseIngresso),
  };
  message.textContent = "";
  const scheduleError = validateSessionSchedule(body);
  if (scheduleError) {
    message.textContent = scheduleError.message;
    scheduleError.field.focus();
    return;
  }
  submit.disabled = true;
  try {
    await request(id ? `/sessoes/${encodeURIComponent(id)}` : "/sessoes", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(body),
    });
    sessionForm.reset();
    sessionForm.classList.add("hidden");
    showSessionNotice(id ? "Sessão atualizada." : "Sessão criada.", true);
    await loadSessions();
  } catch (error) {
    message.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

if (token()) renderSignedIn();
else loadPlays().then(loadSessions);
