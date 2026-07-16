/* ===== modal / confirm ===== */
(function(){
  let overlay;
  function ensureOverlay(){
    if(!overlay){
      overlay=document.createElement('div');
      overlay.className='modal-overlay';
      overlay.innerHTML='<div class="modal-box modal-confirm"><div class="modal-body"><div class="confirm-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 9v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg></div><h3 id="modal-title"></h3><p id="modal-msg" style="color:var(--color-gray-500);margin-top:8px;font-size:var(--fs-sm)"></p></div><div class="modal-footer"><button class="btn btn-outline" id="modal-cancel">Annuler</button><button class="btn btn-danger" id="modal-confirm">Supprimer</button></div></div>';
      document.body.appendChild(overlay);
      document.getElementById('modal-cancel').onclick=()=>closeModal();
      document.getElementById('modal-confirm').onclick=()=>{if(window._modalCb)window._modalCb();closeModal();};
      overlay.addEventListener('click',e=>{if(e.target===overlay)closeModal();});
    }
    return overlay;
  }
  function closeModal(){overlay.classList.remove('open');}
  window.openConfirmModal=function(opts){
    ensureOverlay();
    document.getElementById('modal-title').textContent=opts.title||'Confirmer';
    document.getElementById('modal-msg').textContent=opts.message||'';
    window._modalCb=opts.onConfirm||null;
    overlay.classList.add('open');
  };
})();


