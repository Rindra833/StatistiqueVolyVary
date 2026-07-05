// composant table generique: recherche multicritere, tri, filtres, pagination
// usage: new DataTable({ container, columns, data, pageSize, filters, actions, onExportPdf, onExportExcel, onImportExcel, onAdd })
class DataTable {
  constructor(opts) {
    this.container = opts.container;
    this.columns = opts.columns;          // [{ key, label, sortable, render(row) }]
    this.data = opts.data || [];
    this.pageSize = opts.pageSize || 8;
    this.filters = opts.filters || [];    // [{ key, label, options: [{value,label}] }]
    this.actions = opts.actions || null;  // (row) => html string for row-actions cell
    this.onAdd = opts.onAdd || null;
    this.onExportPdf = opts.onExportPdf || null;
    this.onExportExcel = opts.onExportExcel || null;
    this.onImportExcel = opts.onImportExcel || null;
    this.addLabel = opts.addLabel || 'Ajouter';
    this.searchKeys = opts.searchKeys || (this.columns.map(c => c.key));

    this.state = { search: '', sortKey: null, sortDir: 1, page: 1, activeFilters: {} };
    this.render();
  }

  setData(data) { this.data = data; this.state.page = 1; this.render(); }

  filteredSorted() {
    let rows = this.data.filter(row => {
      const matchSearch = !this.state.search || this.searchKeys.some(k =>
        String(row[k] ?? '').toLowerCase().includes(this.state.search.toLowerCase()));
      const matchFilters = Object.entries(this.state.activeFilters).every(([k, v]) => !v || String(row[k]) === v);
      return matchSearch && matchFilters;
    });
    if (this.state.sortKey) {
      const k = this.state.sortKey;
      rows = rows.slice().sort((a, b) => {
        const av = a[k], bv = b[k];
        if (typeof av === 'number' && typeof bv === 'number') return (av - bv) * this.state.sortDir;
        return String(av ?? '').localeCompare(String(bv ?? '')) * this.state.sortDir;
      });
    }
    return rows;
  }

  render() {
    const rows = this.filteredSorted();
    const totalPages = Math.max(1, Math.ceil(rows.length / this.pageSize));
    this.state.page = Math.min(this.state.page, totalPages);
    const start = (this.state.page - 1) * this.pageSize;
    const pageRows = rows.slice(start, start + this.pageSize);

    this.container.innerHTML = `
      <div class="dt-toolbar">
        <div class="dt-toolbar-left">
          <div class="dt-search">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input type="text" id="dt-search-input" placeholder="Rechercher..." value="${this.state.search}" />
          </div>
          ${this.filters.map(f => `
            <div class="dt-filter">
              <select data-filter-key="${f.key}">
                <option value="">${f.label}</option>
                ${f.options.map(o => `<option value="${o.value}" ${this.state.activeFilters[f.key] === o.value ? 'selected' : ''}>${o.label}</option>`).join('')}
              </select>
            </div>`).join('')}
        </div>
        <div class="dt-toolbar-right">
          ${this.onImportExcel ? `<button class="btn btn-outline btn-sm" id="dt-import"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M17 8l-5-5-5 5M12 3v12"/></svg>Importer Excel</button>` : ''}
          ${this.onExportExcel ? `<button class="btn btn-outline btn-sm" id="dt-export-excel"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>Excel</button>` : ''}
          ${this.onExportPdf ? `<button class="btn btn-outline btn-sm" id="dt-export-pdf"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><path d="M14 2v6h6"/></svg>PDF</button>` : ''}
          ${this.onAdd ? `<button class="btn btn-primary btn-sm" id="dt-add"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>${this.addLabel}</button>` : ''}
        </div>
      </div>
      <div class="table-wrap">
        <table class="dt">
          <thead><tr>
            ${this.columns.map(c => `<th data-key="${c.key}" class="${this.state.sortKey === c.key ? 'sorted' : ''}">${c.label}${c.sortable !== false ? `<span class="sort-arrow">${this.state.sortKey === c.key ? (this.state.sortDir === 1 ? '▲' : '▼') : '▲'}</span>` : ''}</th>`).join('')}
            ${this.actions ? '<th>Actions</th>' : ''}
          </tr></thead>
          <tbody>
            ${pageRows.length === 0 ? `<tr><td colspan="${this.columns.length + (this.actions ? 1 : 0)}" class="dt-empty">Aucun resultat trouve</td></tr>` :
              pageRows.map(row => `<tr>
                ${this.columns.map(c => `<td>${c.render ? c.render(row) : (row[c.key] ?? '')}</td>`).join('')}
                ${this.actions ? `<td><div class="row-actions">${this.actions(row)}</div></td>` : ''}
              </tr>`).join('')}
          </tbody>
        </table>
      </div>
      <div class="dt-pagination">
        <span class="dt-info">${rows.length === 0 ? 0 : start + 1}-${Math.min(start + this.pageSize, rows.length)} sur ${rows.length}</span>
        <div class="dt-pages">
          <button class="dt-page-btn" data-page="${this.state.page - 1}" ${this.state.page === 1 ? 'disabled' : ''}>&laquo;</button>
          ${Array.from({ length: totalPages }, (_, i) => i + 1).filter(p => p === 1 || p === totalPages || Math.abs(p - this.state.page) <= 1)
            .reduce((acc, p, idx, arr) => { if (idx > 0 && p - arr[idx - 1] > 1) acc.push('...'); acc.push(p); return acc; }, [])
            .map(p => p === '...' ? `<span class="dt-page-btn" style="cursor:default">...</span>` : `<button class="dt-page-btn ${p === this.state.page ? 'active' : ''}" data-page="${p}">${p}</button>`).join('')}
          <button class="dt-page-btn" data-page="${this.state.page + 1}" ${this.state.page === totalPages ? 'disabled' : ''}>&raquo;</button>
        </div>
      </div>`;

    this.bindEvents();
  }

  bindEvents() {
    const c = this.container;
    const searchInput = c.querySelector('#dt-search-input');
    let debounce;
    searchInput.addEventListener('input', e => {
      clearTimeout(debounce);
      const val = e.target.value;
      debounce = setTimeout(() => { this.state.search = val; this.state.page = 1; this.render(); c.querySelector('#dt-search-input').focus(); }, 250);
    });

    c.querySelectorAll('[data-filter-key]').forEach(sel => {
      sel.addEventListener('change', e => {
        this.state.activeFilters[sel.dataset.filterKey] = e.target.value;
        this.state.page = 1;
        this.render();
      });
    });

    c.querySelectorAll('th[data-key]').forEach(th => {
      th.addEventListener('click', () => {
        const key = th.dataset.key;
        if (this.state.sortKey === key) this.state.sortDir *= -1;
        else { this.state.sortKey = key; this.state.sortDir = 1; }
        this.render();
      });
    });

    c.querySelectorAll('.dt-page-btn[data-page]').forEach(btn => {
      btn.addEventListener('click', () => {
        const p = Number(btn.dataset.page);
        if (p >= 1) { this.state.page = p; this.render(); }
      });
    });

    if (this.onAdd) c.querySelector('#dt-add').addEventListener('click', this.onAdd);
    if (this.onExportPdf) c.querySelector('#dt-export-pdf').addEventListener('click', () => this.onExportPdf(this.filteredSorted()));
    if (this.onExportExcel) c.querySelector('#dt-export-excel').addEventListener('click', () => this.onExportExcel(this.filteredSorted()));
    if (this.onImportExcel) c.querySelector('#dt-import').addEventListener('click', this.onImportExcel);
  }
}
