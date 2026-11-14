(() => {
  'use strict';
  const U = window.Thindesk;
  const form = document.getElementById('filters');
  let lista = [];
  let carregarEmAndamento = false;
  const abrir = U.ligarModal(carregar);
  function montarOpcoes(id, valores, rotulo) {
    const select = document.getElementById(id);
    const atual = select.value;
    select.replaceChildren();
    const todas = document.createElement('option'); todas.value=''; todas.textContent=rotulo;select.appendChild(todas);
    (valores || []).forEach(v=> {const op=document.createElement('option');op.value=v;op.textContent=v;select.appendChild(op);});
    select.value=atual;
  }
  function graficoMes(valores) {
    const grafico = document.getElementById('monthly-chart');grafico.replaceChildren();
    if (!valores?.length) {grafico.textContent='Nenhum mês disponível para o filtro.';return;}
    const maior = Math.max(1,...valores.map(v=>v.concluidos));
    valores.forEach(p => {
      const coluna = document.createElement('div'); coluna.className='chart-col';
      const numero = document.createElement('span');numero.className='chart-number';numero.textContent=p.concluidos;
      const cont = document.createElement('div');cont.className='chart-bar-wrap';
      const barra = document.createElement('div');barra.className='chart-bar';barra.style.height=(p.concluidos/maior*100)+'%';
      barra.title=p.periodo+': '+p.concluidos+' concluídos';
      cont.appendChild(barra);
      const mes = document.createElement('span');mes.className='chart-month';
      const [ano,m] = p.periodo.split('-');mes.textContent=m+'/'+ano.slice(-2);
      coluna.append(numero,cont,mes);grafico.appendChild(coluna);
    });
  }
  function graficoStatus(valores) {
    const el=document.getElementById('status-chart');el.replaceChildren();
    (valores || []).forEach(s=>{
      const item=document.createElement('div');
      const titulo=document.createElement('div');titulo.className='status-title';
      const nome=document.createElement('span');nome.textContent=U.statusNome(s.status);
      const qtd=document.createElement('span');qtd.textContent=s.quantidade+' ('+Number(s.percentual).toFixed(1)+'%)';
      titulo.append(nome,qtd);
      const faixa=document.createElement('div');faixa.className='track';
      const valor=document.createElement('div');valor.className='fill '+s.status.toLowerCase().replace('_','-');
      valor.style.width=s.percentual+'%';faixa.appendChild(valor);item.append(titulo,faixa);el.appendChild(item);
    });
  }
  function ranking(valores) {
    const el=document.getElementById('types-list');el.replaceChildren();
    if (!valores?.length) { el.textContent='Nenhum tipo de chamado encontrado.';return; }
    valores.forEach((v,i) => {
      const linha=document.createElement('div');linha.className='type-row';
      const nome=document.createElement('span');nome.className='type-name';nome.textContent=(i+1)+'. '+v.tipo;
      const quantidade=document.createElement('span');quantidade.className='type-count';quantidade.textContent=v.atendidos+' concluídos';
      linha.append(nome,quantidade);el.appendChild(linha);
    });
  }
  function tabela(chamados) {
    lista=chamados || [];
    const tbody=document.getElementById('dashboard-table');tbody.replaceChildren();
    if (!lista.length) {const tr=document.createElement('tr');const td=U.adicionarCelula(tr,'Nenhum chamado encontrado com estes filtros.');td.colSpan=6;tbody.appendChild(tr);return;}
    lista.forEach((c,i)=>{
      const tr=document.createElement('tr');U.adicionarCelula(tr,c.descricao);U.adicionarCelula(tr,c.tipo);
      U.adicionarCelula(tr,c.tecnico||'—');U.adicionarStatus(tr,c.status);U.adicionarCelula(tr,U.data(c.dataAbertura));
      const td=document.createElement('td');const b=document.createElement('button');b.className='row-action';b.type='button';b.textContent='Detalhes';
      b.addEventListener('click',()=>abrir(c));td.appendChild(b);tr.appendChild(td);tbody.appendChild(tr);
    });
  }
  async function carregar() {
    if (carregarEmAndamento) return;
    carregarEmAndamento = true;
    const botao=document.getElementById('apply');botao.disabled=true;
    U.aviso(null);
    try {
      const params=new URLSearchParams();new FormData(form).forEach((v,k)=>{if(v) params.set(k,v);});
      const dados=await U.request('/api/dashboard'+(params.size?'?'+params:''));
      const m=dados.indicadores;
      U.texto('kpi-total',m.total);U.texto('kpi-open',m.abertos);U.texto('kpi-progress',m.emAndamento);
      U.texto('kpi-done',m.concluidos);U.texto('kpi-rate','Taxa de resolução: '+m.taxaResolucao+'%');
      U.texto('extra-pending',m.pendentes);U.texto('extra-cancelled',m.cancelados);
      U.texto('extra-time',m.mediaHorasResolucao+' h');U.texto('extra-undated',m.semDataAbertura);
      graficoMes(dados.evolucao);graficoStatus(dados.situacoes);ranking(dados.tipos);tabela(dados.chamados);
      montarOpcoes('filter-type',dados.filtros.tipos,'Todos os tipos');
      montarOpcoes('filter-tech',dados.filtros.tecnicos,'Todos os técnicos');
      U.texto('updated-at','Atualizado em '+new Date(dados.atualizadoEm).toLocaleString('pt-BR'));
    } catch (err) {U.aviso(err.message,true);}
    finally {carregarEmAndamento=false;botao.disabled=false;}
  }
  form.addEventListener('submit',e=>{e.preventDefault();carregar();});
  document.getElementById('clear-filters').addEventListener('click',()=>{form.reset();carregar();});
  carregar();
})();
