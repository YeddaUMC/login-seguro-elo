(() => {
 const U=window.Thindesk;let chamados=[];const tbody=document.getElementById('all-tickets');
 const abrir=U.ligarModal(carregar);
 function tabela() {
   const busca=(document.getElementById('search-tickets').value || '').toLowerCase();tbody.replaceChildren();
   const filtrados=chamados.filter(c=>(c.descricao||'').toLowerCase().includes(busca));
   if(!filtrados.length){const tr=document.createElement('tr'),td=U.adicionarCelula(tr,'Nenhum chamado encontrado.');td.colSpan=6;tbody.appendChild(tr);return;}
   filtrados.forEach(c=>{
     const tr=document.createElement('tr');U.adicionarCelula(tr,c.descricao);U.adicionarCelula(tr,c.tipo);
     U.adicionarCelula(tr,c.tecnico||'—');U.adicionarStatus(tr,c.status);U.adicionarCelula(tr,c.usuario||'—');
     const td=document.createElement('td'),b=document.createElement('button');b.className='row-action';b.textContent='Editar';b.addEventListener('click',()=>abrir(c));td.appendChild(b);tr.appendChild(td);tbody.appendChild(tr);
   });
 }
 async function carregar(){try{chamados=await U.request('/api/chamados');tabela();}catch(e){U.aviso(e.message,true);}}
 document.getElementById('search-tickets').addEventListener('input',tabela);carregar();
})();
