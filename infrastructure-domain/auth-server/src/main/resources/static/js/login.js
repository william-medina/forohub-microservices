document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('loginForm');
  if (!form) return;

  const username = form.querySelector('#username');
  const password = form.querySelector('#password');

  function showError(message, focusEl) {
    let box = document.getElementById('errorBox');

    if (!box) {
      box = document.createElement('div');
      box.id = 'errorBox';
      box.className = 'text-xs sm-500:text-sm leading-3 sm-500:leading-4 px-3 sm-500:px-4 py-3 w-full rounded-lg text-white bg-red-800 mb-4 flex items-center gap-2';
      box.innerHTML = '<span id="errorMessage"></span>';
      form.insertBefore(box, form.firstChild);
    }

    const span = document.getElementById('errorMessage');
    if (span) span.textContent = message;

    if (focusEl && typeof focusEl.focus === 'function') {
      focusEl.focus();
    }

    box.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  function clearError() {
    const box = document.getElementById('errorBox');
    if (box) box.remove();
  }

  form.addEventListener('submit', (e) => {
    const u = username ? username.value.trim() : '';
    const p = password ? password.value.trim() : '';

    if (!u) {
      e.preventDefault();
      showError('El nombre de usuario es requerido.', username);
      return false;
    }

    if (!p) {
      e.preventDefault();
      showError('El password es requerido.', password);
      return false;
    }

    // Si pasa validación: dejar que el formulario se envíe al servidor
  });
});


document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  const usernameInput = document.getElementById("username");
  const passwordInput = document.getElementById("password");

  // Restaurar valor si había un error
  const savedUser = sessionStorage.getItem("loginUsername");
  if (savedUser && window.location.search.includes("error")) {
    usernameInput.value = savedUser;
  }

  // Guardar antes de enviar
  form.addEventListener("submit", () => {
    sessionStorage.setItem("loginUsername", usernameInput.value);
  });
});