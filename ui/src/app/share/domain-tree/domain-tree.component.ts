import { environment } from './../../../environments/environment';
import { DomainService, Domain } from './../../backend/backend.module';
import { Component, OnInit, ViewChild, AfterViewInit, Input, Output, EventEmitter } from '@angular/core';
import { jqxTreeGridComponent } from 'jqwidgets-scripts/jqwidgets-ts/angular_jqxtreegrid';

const ICON_FOLDER = '/assets/img/folder-icon.png';
@Component({
  selector: 'app-domain-tree',
  templateUrl: './domain-tree.component.html',
  styleUrls: ['./domain-tree.component.scss']
})
export class DomainTreeComponent implements OnInit {
  @ViewChild('TreeGrid') treeGrid: jqxTreeGridComponent;
  @Output() selectedChange = new EventEmitter();
  private _selectedDomain;
  localizeObj = {
    filtersearchstring: ''
  };
  source: any =
    {
      dataType: 'json',
      dataFields: [
        { name: 'id', type: 'number' },
        { name: 'label', type: 'string' },
        { name: 'children', type: 'array' },
        { name: 'icon', type: 'string' }
      ],
      hierarchy:
        {
          root: 'children'
        },
      id: 'id',
      // localData: this.domains,
      url: environment.apibaseurl + environment.domainRootUrl,
      addRow: (rowID, rowData, position, parentID, commit) => {
        // synchronize with the server - send insert command
        // call commit with parameter true if the synchronization with the server is successful 
        // and with parameter false if the synchronization failed.
        // you can pass additional argument to the commit callback which represents the new ID if it is generated from a DB.

        this.domainSrv.addChildDomain(+parentID, rowData).subscribe(
          domain => {
            if (domain == null) {
              commit(false);
            } else {
              const newRowID = '' + domain.id;
              commit(true, domain.id);
              this.treeGrid.selectRow(newRowID);
              // edit the new row.
              this.treeGrid.beginRowEdit(newRowID);
              rowData.id = domain.id;
            }
          }
        );
      },
      updateRow: (rowID, rowData, commit) => {
        // synchronize with the server - send update command
        // call commit with parameter true if the synchronization with the server is successful 
        // and with parameter false if the synchronization failed.
        this.domainSrv.updateDomain(rowData).subscribe(
          domain => {
            if (domain == null) {
              commit(false);
            } else {
              commit(true);
            }
          }
        );
      },
      deleteRow: (rowID, commit) => {
        // synchronize with the server - send delete command
        // call commit with parameter true if the synchronization with the server is successful 
        // and with parameter false if the synchronization failed.
        this.domainSrv.deleteDomain(rowID).subscribe(
          _ => {
            commit(true);
          }
        );
      }
    };
  theme = '';
  buttonsObject: any = null;

  dataAdapter = new jqx.dataAdapter(this.source, {
    autoBind: true,
    downloadComplete: function (edata, textStatus, jqXHR) {
      //convert the returned domain json to an array
      if (jqXHR.status !== 200) {
        return [];
      }
      const data = new Array();
      data.push(edata);
      return data;
    },

    beforeLoadComplete: function (records) {
      const formatDomain = (node) => {
        node.icon = ICON_FOLDER;
        node.children.forEach(child => {
          formatDomain(child);
        });
      };
      records.forEach(node => {
        formatDomain(node);
      });
    }
  });
  columns: any[] = [
    { text: 'Label', dataField: 'label' }
  ];

  constructor(private domainSrv: DomainService) { }

  ngOnInit() {
  }
  @Input()
  get selected() {
    return this._selectedDomain;
  }

  set selected(val) {
    if (this._selectedDomain !== val) {
      this._selectedDomain = val;
      this.selectedChange.emit(this._selectedDomain);
    }
  }
  updateButtons(action: string, buttons: any): void {
    switch (action) {
      case 'Select':
        buttons.addButton.jqxButton({ disabled: false });
        buttons.deleteButton.jqxButton({ disabled: false });
        buttons.editButton.jqxButton({ disabled: false });
        break;
      case 'Unselect':
        buttons.addButton.jqxButton({ disabled: true });
        buttons.deleteButton.jqxButton({ disabled: true });
        buttons.editButton.jqxButton({ disabled: true });
        break;
      case 'Edit':
        buttons.addButton.jqxButton({ disabled: true });
        buttons.deleteButton.jqxButton({ disabled: true });
        buttons.editButton.jqxButton({ disabled: true });
        break;
      case 'End Edit':
        buttons.addButton.jqxButton({ disabled: false });
        buttons.deleteButton.jqxButton({ disabled: false });
        buttons.editButton.jqxButton({ disabled: false });
        break;
    }
  }

  renderToolbar = (toolBar) => {
    if (this.buttonsObject) {
      return;
    }
    const toTheme = (className) => {
      if (this.theme === '') { return className; }
      return className + ' ' + className + '-' + this.theme;
    };
    // appends buttons to the status bar.
    const container: any = $('<div style="overflow: hidden; position: relative; height: 100%; width: 100%;"></div>');
    const buttonTemplate: any =
      '<div style="float: left; padding: 3px; margin: 2px;"><div style="margin: 4px; width: 16px; height: 16px;"></div></div>';
    const addButton: any = $(buttonTemplate);
    const editButton: any = $(buttonTemplate);
    const deleteButton: any = $(buttonTemplate);
    container.append(addButton);
    container.append(editButton);
    container.append(deleteButton);
    toolBar.append(container);
    addButton.jqxButton({ cursor: 'pointer', enableDefault: false, disabled: true, height: 25, width: 25 });
    addButton.find('div:first').addClass(toTheme('jqx-icon-plus'));
    addButton.jqxTooltip({ position: 'bottom', content: '添加' });
    editButton.jqxButton({ cursor: 'pointer', disabled: true, enableDefault: false, height: 25, width: 25 });
    editButton.find('div:first').addClass(toTheme('jqx-icon-edit'));
    editButton.jqxTooltip({ position: 'bottom', content: '修改' });
    deleteButton.jqxButton({ cursor: 'pointer', disabled: true, enableDefault: false, height: 25, width: 25 });
    deleteButton.find('div:first').addClass(toTheme('jqx-icon-delete'));
    deleteButton.jqxTooltip({ position: 'bottom', content: '删除' });

    this.buttonsObject = {
      addButton: addButton,
      editButton: editButton,
      deleteButton: deleteButton
    };

    addButton.click((event) => {
      if (!addButton.jqxButton('disabled')) {
        this.treeGrid.expandRow(this.selected);
        // add new empty row.
        this.treeGrid.addRow(null, { icon: ICON_FOLDER, label: '新建工地' }, 'last', this.selected);
        // select the new row and clear the selection.
        this.treeGrid.clearSelection();

        this.updateButtons('add', this.buttonsObject);
      }
    });

    editButton.click(() => {
      if (!editButton.jqxButton('disabled')) {
        this.treeGrid.beginRowEdit(this.selected);
        this.updateButtons('edit', this.buttonsObject);
      }
    });

    deleteButton.click(() => {
      if (!deleteButton.jqxButton('disabled')) {
        const selection = this.treeGrid.getSelection();
        if (selection.length > 1) {
          for (let i = 0; i < selection.length; i++) {
            const key = this.treeGrid.getKey(selection[i]);
            this.treeGrid.deleteRow(key);
          }
        } else {
          this.treeGrid.deleteRow(this.selected);
        }
        this.updateButtons('delete', this.buttonsObject);
      }
    });
  }


  rowSelect(event: any): void {
    const args = event.args;
    this.selected = args.key;
    if (this.buttonsObject !== null) {
      this.updateButtons('Select', this.buttonsObject);
    }
  }

  rowUnselect(event: any): void {
    this.updateButtons('Unselect', this.buttonsObject);
  }

  rowEndEdit(event: any): void {
    this.updateButtons('End Edit', this.buttonsObject);
  }

  rowBeginEdit(event: any): void {
    this.updateButtons('Edit', this.buttonsObject);
  }
  bindingComplete(event: any): void {
    // expand the root node
    const rootDomain = this.dataAdapter.records[0].id;
    if (this.selected === null || isNaN(this.selected)) {
      this.selected = rootDomain;
    }
    if (this.selected && !isNaN(this.selected)) {
      this.treeGrid.selectRow(this.selected);
      // expand all its parents
      this.expandParents(this.selected);
    }
  }
  expandParents(rowKey) {
    const row = this.treeGrid.getRow(rowKey);
    if (!row.expanded) {
      if (row.parent) {
        this.expandParents(row.parent.id);
      }
      if (!row.leaf) {
        this.treeGrid.expandRow(rowKey);
      }
    }
  }
  ready(): void {
  }
}
