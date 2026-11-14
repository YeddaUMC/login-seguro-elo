(() => {
 const U=window.Thindesk;const form=document.getElementById('client-form');
 async function carregar(){try{const dados=await U.request('/api/clientes'),tbody=document.getElementById('clients-list');tbody.replaceChildren();if(!dados.length){const tr=document.createElement('tr'),td=U.adicionarCelula(tr,'Nenhum cliente cadastrado.');td.colSpan=3;tbody.appendChild(tr);}dados.forEach(c=>{const tr=document.createElement('tr');[c.nome,c.setor,c.telefone].forEach(v=>U.adicionarCelula(tr,v||'—'));tbody.appendChild(tr);});}catch(e){U.aviso(e.message,true);}}
 form.addEventListener('submit',async e=>{e.preventDefault();try{await U.request('/api/clientes',{method:'POST',body:JSON.stringify(Object.fromEntries(new FormData(form)))});form.reset();U.aviso('Cliente cadastrado.');carregar();}catch(err){U.aviso(err.message,true);}});
 carregar();
})();
