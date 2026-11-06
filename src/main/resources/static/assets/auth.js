const API = "/api/v1";
const TOKEN_KEY = "bilheteria.token";
const EMAIL_KEY = "bilheteria.email";
const ROLE_KEY = "bilheteria.papel";
const form = document.querySelector("#auth-form");
const message = document.querySelector("#auth-message");
const submit = document.querySelector("#auth-submit");
const nameField = document.querySelector("#name-field");
const emailField = form.elements.email;
const passwordField = form.elements.senha;
const parameters = new URLSearchParams(window.location.search);
const requestedDestination = parameters.get("next");
const destination = requestedDestination?.startsWith("/") && !requestedDestination.startsWith("//")
  ? requestedDestination
  : "/";
let mode = "login";

function setMode(nextMode) {
  mode = nextMode;
  const registering = mode === "register";
  document.querySelector("#login-tab").classList.toggle("active", !registering);
  document.querySelector("#register-tab").classList.toggle("active", registering);
  document.querySelector("#login-tab").setAttribute("aria-selected", String(!registering));
  document.querySelector("#register-tab").setAttribute("aria-selected", String(registering));
  nameField.classList.toggle("hidden", !registering);
  form.elements.nome.required = registering;
  passwordField.autocomplete = registering ? "new-password" : "current-password";
  submit.innerHTML = registering
    ? 'Criar conta <span aria-hidden="true">↗</span>'
    : 'Entrar <span aria-hidden="true">↗</span>';
  message.textContent = "";
  message.classList.remove("success-message");
  document.querySelector("#auth-title").textContent = registering
    ? "Crie sua conta"
    : "Acesse sua conta";
}

async function request(path, body) {
  const response = await fetch(`${API}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (response.status === 401) throw new Error("E-mail ou senha incorretos.");
  if (response.status === 409) throw new Error("Este e-mail já possui uma conta cadastrada.");
  if (!response.ok) throw new Error("Não foi possível concluir. Confira os dados e tente novamente.");
  return response.json();
}

document.querySelector("#login-tab").addEventListener("click", () => setMode("login"));
document.querySelector("#register-tab").addEventListener("click", () => setMode("register"));

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(form));
  message.textContent = "";
  message.classList.remove("success-message");
  if (mode === "register" && !data.nome.trim()) {
    message.textContent = "Informe seu nome para criar a conta.";
    form.elements.nome.focus();
    return;
  }

  submit.disabled = true;
  try {
    if (mode === "register") {
      await request("/usuarios", data);
      setMode("login");
      emailField.value = data.email;
      message.textContent = "Conta criada. Entre com seu e-mail e senha.";
      message.classList.add("success-message");
      passwordField.focus();
      return;
    }

    const result = await request("/autenticacao", {
      email: data.email,
      senha: data.senha,
    });
    sessionStorage.setItem(TOKEN_KEY, result.token);
    sessionStorage.setItem(EMAIL_KEY, data.email);
    sessionStorage.setItem(ROLE_KEY, result.papel || "CLIENTE");
    window.location.assign(destination);
  } catch (error) {
    message.textContent = error instanceof TypeError
      ? "Não foi possível conectar ao servidor. Tente novamente."
      : error.message;
  } finally {
    submit.disabled = false;
  }
});
