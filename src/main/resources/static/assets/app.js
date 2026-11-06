const API = "/api/v1";
const TOKEN_KEY = "bilheteria.token";
const EMAIL_KEY = "bilheteria.email";
const ROLE_KEY = "bilheteria.papel";
const notice = document.querySelector("#notice");
const catalogToolbar = document.querySelector("#catalog-toolbar");
const catalogState = document.querySelector("#catalog-state");
const playGrid = document.querySelector("#play-grid");
const playForm = document.querySelector("#play-form");
let playsCache = [];

function token() {
  return sessionStorage.getItem(TOKEN_KEY);
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
    if (path === "/pecas") {
      throw new Error(
        "O servidor não liberou a programação pública. Reinicie a aplicação e atualize a página.",
      );
    }
    throw new Error("Entre na sua conta para continuar.");
  }
  if (!response.ok) {
    let serverMessage = "";
    try {
      serverMessage = (await response.json()).mensagem || "";
    } catch {
      serverMessage = "";
    }
    const messages = {
      400: "Confira os dados informados e tente novamente.",
      403: "Você não tem permissão para realizar esta ação.",
      404: "O item solicitado não foi encontrado.",
      409: "A operação não pode ser concluída. Confira os dados e tente novamente.",
    };
    if (response.status === 409 && path === "/usuarios") {
      throw new Error("Este e-mail já possui uma conta cadastrada.");
    }
    throw new Error(
      serverMessage || messages[response.status] || "Não foi possível concluir a solicitação. Tente novamente.",
    );
  }
  return response.status === 204 ? null : response.json();
}

function renderSignedIn() {
  catalogToolbar.classList.toggle("hidden", !isAdmin());
  document.querySelector("#add-play-toggle").classList.toggle("hidden", !isAdmin());
  document.querySelector("#nav-auth").classList.add("hidden");
  document.querySelector("#nav-tickets").classList.toggle("hidden", isAdmin());
  document.querySelector("#nav-user").classList.remove("hidden");
  loadPlays();
}

function signOut(showMessage = true) {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(EMAIL_KEY);
  sessionStorage.removeItem(ROLE_KEY);
  catalogToolbar.classList.add("hidden");
  playForm.classList.add("hidden");
  playGrid.classList.add("hidden");
  catalogState.classList.remove("hidden");
  catalogState.innerHTML =
    '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando a programação…</p>';
  document.querySelector("#nav-auth").classList.remove("hidden");
  document.querySelector("#nav-tickets").classList.add("hidden");
  document.querySelector("#nav-user").classList.add("hidden");
  if (showMessage) showNotice("Você saiu da sua conta.", true);
  loadPlays();
}

async function loadPlays() {
  catalogState.classList.remove("hidden");
  catalogState.innerHTML =
    '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando a programação…</p>';
  playGrid.classList.add("hidden");
  try {
    playsCache = await request("/pecas");
    renderPlays(playsCache);
  } catch (error) {
    playsCache = [];
    catalogState.innerHTML = `<div class="empty-mark" aria-hidden="true">✳</div><p>${escapeHtml(error.message)}</p>`;
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
    const card = document.createElement(isAdmin() ? "article" : "a");
    card.className = `play-card${isAdmin() ? " admin-play-card" : ""}`;
    const sessionsUrl = `/sessoes.html?peca=${encodeURIComponent(play.id)}`;
    if (!isAdmin()) {
      card.setAttribute("aria-label", `Ver sessões para ${play.titulo}`);
      card.href = sessionsUrl;
    } else {
      card.setAttribute("role", "link");
      card.setAttribute("tabindex", "0");
      card.setAttribute("aria-label", `Gerenciar sessões de ${play.titulo}`);
      card.addEventListener("click", (event) => {
        if (event.target.closest("button, a")) return;
        window.location.assign(sessionsUrl);
      });
      card.addEventListener("keydown", (event) => {
        if (event.target !== card || !["Enter", " "].includes(event.key)) return;
        event.preventDefault();
        window.location.assign(sessionsUrl);
      });
    }
    card.innerHTML = `
      <span class="play-number">Em cartaz · ${String(index + 1).padStart(2, "0")}</span>
      <h3>${escapeHtml(play.titulo)}</h3>
      <p>${escapeHtml(play.descricao)}</p>
      <div class="play-meta">
        <span>${escapeHtml(play.classificacao)}</span>
        <span>${Number(play.duracaoMinutos)} min</span>
      </div>${isAdmin()
        ? `<div class="play-admin-actions">
            <button class="text-action edit-play" type="button" data-play-id="${escapeHtml(play.id)}">Editar peça</button>
            <button class="text-action danger remove-play" type="button" data-play-id="${escapeHtml(play.id)}">Remover peça</button>
          </div>
          <a class="play-select" href="${sessionsUrl}">Gerenciar sessões <span aria-hidden="true">→</span></a>`
        : '<span class="play-select">Ver sessões <span aria-hidden="true">→</span></span>'}`;
    playGrid.append(card);
  });
  catalogState.classList.add("hidden");
  playGrid.classList.remove("hidden");
}

function openPlayForm(play = null) {
  playForm.reset();
  playForm.elements.pecaId.value = play?.id || "";
  if (play) {
    playForm.elements.titulo.value = play.titulo;
    playForm.elements.descricao.value = play.descricao;
    playForm.elements.duracaoMinutos.value = play.duracaoMinutos;
    playForm.elements.classificacao.value = play.classificacao;
  }
  document.querySelector("#play-form-eyebrow").textContent = play ? "Editar peça" : "Novo espetáculo";
  document.querySelector("#play-form-title").textContent = play
    ? "Atualize os dados da peça"
    : "Coloque uma peça em cena";
  document.querySelector("#play-submit").innerHTML = play
    ? "Salvar alterações"
    : 'Salvar peça <span aria-hidden="true">↗</span>';
  document.querySelector("#play-message").textContent = "";
  playForm.classList.remove("hidden");
  playForm.scrollIntoView({ behavior: "smooth", block: "center" });
}

function escapeHtml(value) {
  return String(value ?? "").replace(
    /[&<>"']/g,
    (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char],
  );
}

document.querySelector("#nav-user").addEventListener("click", () => signOut());
document.querySelector("#add-play-toggle").addEventListener("click", () => openPlayForm());
document
  .querySelector("#close-play-form")
  .addEventListener("click", () => playForm.classList.add("hidden"));

playForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const message = document.querySelector("#play-message");
  const submit = playForm.querySelector('button[type="submit"]');
  const data = Object.fromEntries(new FormData(playForm));
  const id = data.pecaId;
  message.textContent = "";
  submit.disabled = true;
  try {
    const body = {
      titulo: data.titulo,
      descricao: data.descricao,
      duracaoMinutos: Number(data.duracaoMinutos),
      classificacao: data.classificacao,
    };
    await request(id ? `/pecas/${encodeURIComponent(id)}` : "/pecas", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(body),
    });
    playForm.reset();
    playForm.classList.add("hidden");
    showNotice(id ? "Peça atualizada." : "Peça cadastrada e adicionada à programação.", true);
    await loadPlays();
  } catch (error) {
    message.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

playGrid.addEventListener("click", async (event) => {
  const editButton = event.target.closest(".edit-play[data-play-id]");
  if (editButton) {
    const play = playsCache.find((item) => item.id === editButton.dataset.playId);
    if (play) openPlayForm(play);
    return;
  }
  const removeButton = event.target.closest(".remove-play[data-play-id]");
  if (!removeButton || !window.confirm("Remover esta peça? Só é possível remover peças sem sessões.")) return;
  removeButton.disabled = true;
  try {
    await request(`/pecas/${encodeURIComponent(removeButton.dataset.playId)}`, { method: "DELETE" });
    showNotice("Peça removida da programação.", true);
    await loadPlays();
  } catch (error) {
    showNotice(error.message);
    removeButton.disabled = false;
  }
});

if (token()) renderSignedIn();
else loadPlays();
