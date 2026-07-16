/* ===== toast ===== */
(function(){
  let stack;
  function ensureStack(){
    if(!stack){
      stack=document.createElement('div');
      stack.className='toast-stack';
      document.body.appendChild(stack);
    }
    return stack;
  }
  window.toast=function(msg,type){
    type=type||'info';
    const s=ensureStack();
    const el=document.createElement('div');
    el.className='toast '+type;
    const icons={success:'&#10003;',error:'&#10007;',warning:'&#9888;',info:'&#8505;'};
    el.innerHTML='<span>'+(icons[type]||icons.info)+'</span><span class="toast-text">'+msg+'</span><span class="toast-close" onclick="this.parentElement.remove()">&times;</span>';
    s.appendChild(el);
    requestAnimationFrame(()=>requestAnimationFrame(()=>el.classList.add('show')));
    setTimeout(()=>{el.classList.remove('show');setTimeout(()=>el.remove(),300);},3500);
  };
  window._toastQueue=[];
  window.toastQueue=function(msg,type){window._toastQueue.push({msg,type});};
  window.toastConsume=function(){window._toastQueue.forEach(t=>toast(t.msg,t.type));window._toastQueue=[];};
})();
