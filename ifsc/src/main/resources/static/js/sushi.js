// Event delegation para ações simples (demo)
document.addEventListener('click', (ev) => {
  const btn = ev.target.closest('button, a');
  if (!btn) return;

  // cardápio: ativar/desativar (mock)
  if (btn.dataset.action === 'ativar' || btn.dataset.action === 'desativar') {
    const id = btn.dataset.id;
    console.log(`[produto:${id}] ação: ${btn.dataset.action}`);
    alert(`Produto ${id} — ação: ${btn.dataset.action} (mock)`);
  }

  // pedidos: finalizar / cancelar (mock)
  if (btn.dataset.action === 'finalizar' || btn.dataset.action === 'cancelar') {
    const id = btn.dataset.id;
    console.log(`[pedido:${id}] ação: ${btn.dataset.action}`);
    alert(`Pedido ${id} — ação: ${btn.dataset.action} (mock)`);
  }
});
