(() => {
  'use strict';
  const selected = location.pathname.replace(/^\//, '') || 'dashboard';
  document.querySelectorAll('[data-nav]').forEach(a => {
    if (a.dataset.nav === selected) { a.classList.add('active'); a.setAttribute('aria-current', 'page'); }
  });
  const menu = document.querySelector('.mobile-nav');
  menu?.addEventListener('click', () => {
    const links = document.querySelector('.nav-links');
    const aberto = links.classList.toggle('open');
    menu.setAttribute('aria-expanded', String(aberto));
  });
  function texto(id, valor) { const e = document.getElementById(id); if (e) e.textContent = valor; }
  function statusNome(s) {
    return ({ABERTO:'Aberto',EM_ANDAMENTO:'Em andamento',CONCLUIDO:'Concluído',CANCELADO:'Cancelado'})[s] || s || 'Não informado';
  }
  function statusCodigo(s) {
    const semAcento = String(s || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toUpperCase().replace(/[ -]/g,'_');
    return ({ABERTA:'ABERTO',PENDENTE:'ABERTO',RESOLVIDO:'CONCLUIDO',FINALIZADO:'CONCLUIDO',FECHADO:'CONCLUIDO'})[semAcento] || semAcento;
  }
  function data(s) { if (!s) return 'Não informada'; const d = new Date(s); return Number.isNaN(d.getTime()) ? 'Não informada' : d.toLocaleDateString('pt-BR'); }
  function aviso(mensagem, erro=false, id='feedback') {
    const el = document.getElementById(id); if (!el) return;
    el.textContent = mensagem || ''; el.className = erro ? 'alert error' : 'alert success'; el.hidden = !mensagem;
  }
  async function request(url, options={}) {
    const metaToken = document.querySelector('meta[name="_csrf"]');
    const metaHeader = document.querySelector('meta[name="_csrf_header"]');
    const headers = new Headers(options.headers || {});
    if (options.body) headers.set('Content-Type','application/json');
    if (options.method && !['GET','HEAD'].includes(options.method.toUpperCase()) && metaToken && metaHeader)
      headers.set(metaHeader.content, metaToken.content);
    const response = await fetch(url, {credentials:'same-origin',...options,headers});
    if (response.redirected && response.url.includes('/login')) throw new Error('Sua sessão expirou. Entre novamente.');
    if (!response.ok) {
      const body = await response.json().catch(() => ({}));
      throw new Error(body.mensagem || (response.status === 403 ? 'Acesso negado ou token de segurança inválido.' : 'Falha ao consultar o servidor (' + response.status + ').'));
    }
    if (response.status === 204) return null;
    return response.json();
  }
  function adicionarCelula(tr, valor) { const td = document.createElement('td'); td.textContent=valor ?? '—';tr.appendChild(td);return td; }
  function adicionarStatus(tr, valor) {
    const td = document.createElement('td');
    const badge = document.createElement('span'); const code = statusCodigo(valor);
    badge.className = 'status-pill ' + code.toLowerCase().replace('_','-');badge.textContent=statusNome(code);
    td.appendChild(badge);tr.appendChild(td);
  }
  function ligarModal(aoSalvar) {
    const dialog = document.getElementById('ticket-dialog'); const form = document.getElementById('ticket-form');
    if (!dialog || !form) return;
    document.querySelectorAll('.close-dialog').forEach(b => b.addEventListener('click',() => dialog.close()));
    document.getElementById('new-ticket')?.addEventListener('click', () => abrir());
    function abrir(chamado=null) {
      form.reset();
      for (const [chave,valor] of Object.entries(chamado || {})) {
        const item = form.elements.namedItem(chave);
        if (item && !('length' in item)) item.value = chave === 'status' ? statusCodigo(valor) : (valor ?? '');
      }
      texto('dialog-title', chamado ? 'Editar chamado' : 'Novo chamado');
      aviso(null,true,'form-error');
      dialog.showModal();
    }
    form.addEventListener('submit', async e => {
      e.preventDefault();
      const campos = Object.fromEntries(new FormData(form));
      const id = campos.id; delete campos.id;
      const botao = document.getElementById('submit-ticket');botao.disabled=true;
      try {
        await request(id ? `/api/chamados/${encodeURIComponent(id)}` : '/api/chamados', {
          method:id ? 'PUT':'POST',body:JSON.stringify(campos)
        });
        dialog.close(); await aoSalvar();
        aviso('Chamado salvo com sucesso.');
      } catch(err) { aviso(err.message,true,'form-error'); }
      finally { botao.disabled=false; }
    });
    return abrir;
  }
  window.Thindesk = {request,texto,statusNome,statusCodigo,data,aviso,adicionarCelula,adicionarStatus,ligarModal};
})();
