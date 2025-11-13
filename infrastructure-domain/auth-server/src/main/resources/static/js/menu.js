document.addEventListener('DOMContentLoaded', () => {
  const menuBtn = document.getElementById('menuButton');
  const mobileMenu = document.getElementById('mobileMenu');

  if (!menuBtn || !mobileMenu) return;

  // Detecta si el dispositivo soporta hover (desktop)
  const supportsHover = window.matchMedia('(hover: hover)').matches;

  // Para dispositivos táctiles: click para alternar
  if (!supportsHover) {
    menuBtn.addEventListener('click', (ev) => {
      ev.stopPropagation();
      mobileMenu.classList.toggle('hidden');
      const expanded = mobileMenu.classList.contains('hidden') ? 'false' : 'true';
      menuBtn.setAttribute('aria-expanded', expanded);
    });
  } else {
    // En desktop, damos accesibilidad por teclado (Enter / Space)
    menuBtn.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        mobileMenu.classList.toggle('hidden');
        menuBtn.setAttribute('aria-expanded', mobileMenu.classList.contains('hidden') ? 'false' : 'true');
      }
    });
  }

  // Cerrar cuando se hace clic fuera
  document.addEventListener('click', (e) => {
    if (!menuBtn.contains(e.target) && !mobileMenu.contains(e.target)) {
      mobileMenu.classList.add('hidden');
      menuBtn.setAttribute('aria-expanded', 'false');
    }
  });

  // Cerrar con Escape
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      mobileMenu.classList.add('hidden');
      menuBtn.setAttribute('aria-expanded', 'false');
    }
  });
});