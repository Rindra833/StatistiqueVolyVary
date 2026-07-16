// export cote client, sans dependance externe
// excel: export CSV lisible par Excel/LibreOffice (utf-8 + BOM)
function exportToExcel(rows, columns, filename) {
  const headers = columns.map(c => c.label);
  const keys = columns.map(c => c.key);
  const lines = [headers.join(';')].concat(
    rows.map(r => keys.map(k => String(r[k] ?? '').replace(/;/g, ',')).join(';'))
  );
  const blob = new Blob(['\uFEFF' + lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `${filename}.csv`;
  link.click();
  toast('Export Excel genere', 'success');
}

// pdf: ouvre une fenetre imprimable, l'utilisateur choisit "Enregistrer en PDF"
function exportToPdf(rows, columns, title) {
  const headers = columns.map(c => c.label);
  const keys = columns.map(c => c.key);
  const win = window.open('', '_blank');
  win.document.write(`
    <html><head><title>${title}</title>
    <style>
      body { font-family: Arial, sans-serif; padding: 24px; color: #1E293B; }
      h1 { font-size: 18px; margin-bottom: 16px; }
      table { width: 100%; border-collapse: collapse; }
      th, td { border: 1px solid #E2E8F0; padding: 8px 10px; font-size: 12px; text-align: left; }
      th { background: #F1F5F9; }
    </style></head><body>
    <h1>${title} - VOLY VARY</h1>
    <table><thead><tr>${headers.map(h => `<th>${h}</th>`).join('')}</tr></thead>
    <tbody>${rows.map(r => `<tr>${keys.map(k => `<td>${r[k] ?? ''}</td>`).join('')}</tr>`).join('')}</tbody>
    </table></body></html>`);
  win.document.close();
  win.focus();
  win.print();
}
