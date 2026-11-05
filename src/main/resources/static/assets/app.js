const API = "/api/v1";
const TOKEN_KEY = "bilheteria.token";
const EMAIL_KEY = "bilheteria.email";
const form = document.querySelector("#auth-form");
const notice = document.querySelector("#notice");
const authPanel = document.querySelector("#auth-panel");
const catalogToolbar = document.querySelector("#catalog-toolbar");
const catalogState = document.querySelector("#catalog-state");
const playGrid = document.querySelector("#play-grid");
const playForm = document.querySelector("#play-form");
let authMode = "login";

function token() { return sessionStorage.getItem(TOKEN_KEY); }

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
  if (response.status === 401 && token()) {
    signOut(false);
    throw new Error("Sua sessão expirou. Entre novamente para continuar.");
  }
  if (!response.ok) {
    const messages = {
      400: "Confira os dados informados e tente novamente.",
      401: "E-mail ou senha incorretos.",
      409: "Este e-mail já possui uma conta cadastrada."
    };
    throw new Error(messages[response.status] || "Não foi possível concluir a solicitação. Tente novamente.");
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
  document.querySelector("[name=senha]").autocomplete = registering ? "new-password" : "current-password";
  document.querySelector("#auth-submit").innerHTML = registering ? "Criar minha conta <span aria-hidden=\"true\">↗</span>" : "Entrar na bilheteria <span aria-hidden=\"true\">↗</span>";
  document.querySelector("#auth-message").textContent = "";
}

function renderSignedIn() {
  authPanel.classList.add("hidden");
  catalogToolbar.classList.remove("hidden");
  document.querySelector("#nav-auth").classList.add("hidden");
  document.querySelector("#nav-user").classList.remove("hidden");
  const email = sessionStorage.getItem(EMAIL_KEY) || "";
  document.querySelector("#welcome-message").textContent = email ? `Olá, ${email}. Que bom ter você por aqui.` : "Que bom ter você por aqui.";
  loadPlays();
}

function signOut(showMessage = true) {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(EMAIL_KEY);
  authPanel.classList.remove("hidden");
  catalogToolbar.classList.add("hidden");
  playForm.classList.add("hidden");
  playGrid.classList.add("hidden");
  catalogState.classList.remove("hidden");
  catalogState.innerHTML = '<div class="empty-mark" aria-hidden="true">✳</div><p>Faça login para descobrir as peças disponíveis.</p>';
  document.querySelector("#nav-auth").classList.remove("hidden");
  document.querySelector("#nav-user").classList.add("hidden");
  if (showMessage) showNotice("Você saiu da sua conta.", true);
}

async function loadPlays() {
  catalogState.classList.remove("hidden");
  catalogState.innerHTML = '<div class="empty-mark" aria-hidden="true">✳</div><p>Carregando a programação…</p>';
  playGrid.classList.add("hidden");
  try {
    const plays = await request("/pecas");
    renderPlays(plays);
  } catch (error) {
    catalogState.innerHTML = `<div class="empty-mark" aria-hidden="true">✳</div><p>${escapeHtml(error.message)}</p>`;
  }
}

function renderPlays(plays) {
  playGrid.replaceChildren();
  if (!plays.length) {
    catalogState.innerHTML = '<div class="empty-mark" aria-hidden="true">✳</div><p>Ainda não há peças cadastradas. Que tal incluir a primeira?</p>';
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
  return String(value ?? "").replace(/[&<>"']/g, char => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char]);
}

document.querySelector("#login-tab").addEventListener("click", () => setAuthMode("login"));
document.querySelector("#register-tab").addEventListener("click", () => setAuthMode("register"));
document.querySelector("#nav-auth").addEventListener("click", () => document.querySelector("#programacao").scrollIntoView({ behavior: "smooth" }));
document.querySelector("#nav-user").addEventListener("click", () => signOut());
document.querySelector("#add-play-toggle").addEventListener("click", () => {
  playForm.classList.toggle("hidden");
  if (!playForm.classList.contains("hidden")) playForm.scrollIntoView({ behavior: "smooth", block: "center" });
});
document.querySelector("#close-play-form").addEventListener("click", () => playForm.classList.add("hidden"));

form.addEventListener("submit", async event => {
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
      const result = await request("/autenticacao", { method: "POST", body: JSON.stringify({ email: data.email, senha: data.senha }) });
      sessionStorage.setItem(TOKEN_KEY, result.token);
      sessionStorage.setItem(EMAIL_KEY, data.email);
      form.reset();
      renderSignedIn();
    }
  } catch (error) {
    message.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

playForm.addEventListener("submit", async event => {
  event.preventDefault();
  const message = document.querySelector("#play-message");
  const submit = playForm.querySelector("button[type=submit]");
  const data = Object.fromEntries(new FormData(playForm));
  message.textContent = "";
  submit.disabled = true;
  try {
    await request("/pecas", { method: "POST", body: JSON.stringify({ ...data, duracaoMinutos: Number(data.duracaoMinutos) }) });
    playForm.reset();
    playForm.classList.add("hidden");
    showNotice("Peça cadastrada e adicionada à programação.", true);
    await loadPlays();
  } catch (error) {
    message.textContent = error.message;
  } finally {
    submit.disabled = false;
  }
});

if (token()) renderSignedIn();
