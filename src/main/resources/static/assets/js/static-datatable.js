class StaticDataTable {
  constructor(options) {
    this.root = document.getElementById(options.rootId);
    this.columns = options.columns;
    this.pageSize = options.pageSize || 8;
    this.actionsTemplateId = options.actionsTemplateId;
    this.data = [];
    this.state = { search: '', page: 1, sortKey: null, sortDirection: 1, filters: {} };

    this.body = this.root.querySelector('tbody');
    this.info = this.root.querySelector('[data-table-info]');
    this.pages = this.root.querySelector('[data-table-pages]');
    this.bindEvents();
    this.render();
  }

  setData(data) {
    this.data = Array.isArray(data) ? data : [];
    this.state.page = 1;
    this.render();
  }

  filteredSorted() {
    const search = this.state.search.toLowerCase();
    let rows = this.data.filter(row => {
      const matchesSearch = !search || this.columns.some(column =>
        String(row[column.key] ?? '').toLowerCase().includes(search));
      const matchesFilters = Object.entries(this.state.filters)
        .every(([key, value]) => !value || String(row[key] ?? '') === value);
      return matchesSearch && matchesFilters;
    });

    if (this.state.sortKey) {
      const key = this.state.sortKey;
      rows = rows.slice().sort((left, right) => {
        const a = left[key];
        const b = right[key];
        if (typeof a === 'number' && typeof b === 'number') {
          return (a - b) * this.state.sortDirection;
        }
        return String(a ?? '').localeCompare(String(b ?? ''), 'fr') * this.state.sortDirection;
      });
    }
    return rows;
  }

  render() {
    const rows = this.filteredSorted();
    const totalPages = Math.max(1, Math.ceil(rows.length / this.pageSize));
    this.state.page = Math.min(this.state.page, totalPages);
    const start = (this.state.page - 1) * this.pageSize;
    const visibleRows = rows.slice(start, start + this.pageSize);

    this.body.replaceChildren();
    if (visibleRows.length === 0) {
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.colSpan = this.columns.length + (this.actionsTemplateId ? 1 : 0);
      cell.className = 'dt-empty';
      cell.textContent = 'Aucun résultat trouvé';
      row.appendChild(cell);
      this.body.appendChild(row);
    } else {
      visibleRows.forEach(item => this.body.appendChild(this.createRow(item)));
    }

    const first = rows.length === 0 ? 0 : start + 1;
    const last = Math.min(start + this.pageSize, rows.length);
    this.info.textContent = `${first}-${last} sur ${rows.length}`;
    this.renderPagination(totalPages);
  }

  createRow(item) {
    const row = document.createElement('tr');
    this.columns.forEach(column => {
      const cell = document.createElement('td');
      if (column.render) column.render(cell, item);
      else cell.textContent = item[column.key] ?? '';
      row.appendChild(cell);
    });

    if (this.actionsTemplateId) {
      const cell = document.createElement('td');
      const actions = document.getElementById(this.actionsTemplateId).content.cloneNode(true);
      actions.querySelector('[data-edit]').dataset.edit = item.id;
      actions.querySelector('[data-delete]').dataset.delete = item.id;
      cell.appendChild(actions);
      row.appendChild(cell);
    }
    return row;
  }

  renderPagination(totalPages) {
    this.pages.replaceChildren();
    this.pages.appendChild(this.createPageButton('«', this.state.page - 1, this.state.page === 1));
    for (let page = 1; page <= totalPages; page += 1) {
      const button = this.createPageButton(String(page), page, false);
      button.classList.toggle('active', page === this.state.page);
      this.pages.appendChild(button);
    }
    this.pages.appendChild(this.createPageButton('»', this.state.page + 1, this.state.page === totalPages));
  }

  createPageButton(label, page, disabled) {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'dt-page-btn';
    button.textContent = label;
    button.disabled = disabled;
    button.addEventListener('click', () => {
      this.state.page = page;
      this.render();
    });
    return button;
  }

  bindEvents() {
    this.root.querySelector('[data-table-search]')?.addEventListener('input', event => {
      this.state.search = event.target.value;
      this.state.page = 1;
      this.render();
    });

    this.root.querySelectorAll('[data-filter-key]').forEach(select => {
      select.addEventListener('change', () => {
        this.state.filters[select.dataset.filterKey] = select.value;
        this.state.page = 1;
        this.render();
      });
    });

    this.root.querySelectorAll('[data-sort-key]').forEach(header => {
      header.addEventListener('click', () => {
        const key = header.dataset.sortKey;
        if (this.state.sortKey === key) this.state.sortDirection *= -1;
        else {
          this.state.sortKey = key;
          this.state.sortDirection = 1;
        }
        this.render();
      });
    });
  }
}
