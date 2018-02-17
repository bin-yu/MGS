webpackJsonp(["incidents.module"],{

/***/ "../../../../../src/app/incidents/incident/incident.component.html":
/***/ (function(module, exports) {

module.exports = "<h2>\n  {{isAdd?\"新建事件\":\"修改事件：\" + incident.id}}</h2>\n<form name=\"incidentForm\">\n  <div class=\"form-group row\">\n    <label for=\"category\" class=\"col-lg-2 col-form-label\">类型</label>\n    <div class=\"col-lg-3 col-md-3 col-sm-6\">\n      <select class=\"form-control\" id=\"category\" name=\"category\" [(ngModel)]=\"incident.category\">\n        <option value='BLACK'>违规事件</option>\n        <option value='RED'>表扬事件</option>\n      </select>\n    </div>\n  </div>\n  <div class=\"form-group row\">\n    <label for=\"severity\" class=\"col-lg-2 col-form-label\">级别</label>\n    <div class=\"col-lg-10\">\n      <div class=\"slidecontainer\">\n        <input type=\"range\" min=\"1\" max=\"10\" value=\"5\" class=\"slider\" id=\"severity\" name=\"severity\" [(ngModel)]=\"incident.severity\">\n      </div>\n    </div>\n  </div>\n  <div class=\"form-group row\">\n    <label for=\"happenTime\" class=\"col-lg-2 col-form-label\">发生时间</label>\n    <!-- <div class=\"col-lg-4\">\n    <input type=\"date\" class=\"form-control\" id=\"happenTime\" name=\"happenTime\" placeholder=\"输入发生时间\" max=\"{{maxDate | date: 'yyyy-MM-dd'}}\" [(ngModel)]=\"incident.happenTime\">\n    </div>\n    <div class=\"col-lg-6\"></div> -->\n    \n    <div class=\"col-lg-3 col-md-3 col-sm-6\">\n        <div class=\"input-group\">\n          <input class=\"form-control\" placeholder=\"yyyy-mm-dd\"\n                 name=\"dp\" [(ngModel)]=\"incident.happenTime\" [maxDate]=\"maxDate\" ngbDatepicker #d=\"ngbDatepicker\">\n          <div class=\"input-group-append\">\n            <button class=\"btn btn-outline-secondary calendar-img-outer\" (click)=\"d.toggle()\" type=\"button\">\n              <img class=\"calendar-img\" src=\"assets/img/calendar-icon.png\" />\n            </button>\n          </div>\n        </div>\n      </div>\n      <!-- <input matInput class=\"form-control\" [max]=\"maxDate\" [matDatepicker]=\"picker\" id=\"happenTime\" name=\"happenTime\" placeholder=\"输入发生时间\"\n        [(ngModel)]=\"incident.happenTime\">\n\n    </div>\n    <div class=\"col-lg-1\">\n      <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\n      <mat-datepicker #picker></mat-datepicker> -->\n    \n  </div>\n  <div class=\"form-group row\">\n    <label for=\"location\" class=\"col-lg-2 col-form-label\">发生地点</label>\n    <div class=\"col-lg-10\">\n      <input type=\"text\" class=\"form-control \" id=\"location\" name=\"location\" placeholder=\"输入发生地点\" [(ngModel)]=\"incident.location\">\n    </div>\n  </div>\n  <div class=\"form-group row\">\n    <label for=\"subject\" class=\"col-lg-2 col-form-label\">民工编号</label>\n    <div class=\"col-lg-10\">\n      <input type=\"text\" class=\"form-control\" id=\"subject\" name=\"subject\" placeholder=\"输入民工编号\" [(ngModel)]=\"incident.subject.id\">\n    </div>\n  </div>\n  <div class=\"form-group row\">\n    <label for=\"title\" class=\"col-lg-2 col-form-label\">事件概要</label>\n    <div class=\"col-lg-10\">\n      <input type=\"text\" class=\"form-control\" id=\"title\" name=\"title\" placeholder=\"输入事件概要\" [(ngModel)]=\"incident.title\">\n    </div>\n  </div>\n  <div class=\"form-group row\">\n    <label for=\"description\" class=\"col-lg-2 col-form-label\">详细描述</label>\n    <div class=\"col-lg-10\">\n      <textarea id=\"description\" rows=\"6\" class=\"form-control\" name=\"description\" placeholder=\"输入详细描述\" [(ngModel)]=\"incident.description\"></textarea>\n    </div>\n  </div>\n  <div class=\"container-fluid\">\n      <button type=\"button\" class=\" btn btn-primary btn-lg submit-btn\" (click)=\"onSubmit()\">提交</button>\n      <button type=\"button\" class=\" btn btn-primary btn-lg submit-btn\" routerLink=\"../\">取消</button>\n  </div>\n</form>"

/***/ }),

/***/ "../../../../../src/app/incidents/incident/incident.component.scss":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".slidecontainer {\n  width: 100%;\n  /* Width of the outside container */ }\n\n/* The slider itself */\n\n.slider {\n  -webkit-appearance: none;\n  /* Override default CSS styles */\n  width: 100%;\n  /* Full-width */\n  height: 25px;\n  /* Specified height */\n  outline: none;\n  /* Remove outline */\n  background: -webkit-gradient(linear, left top, right top, from(green), to(red));\n  background: linear-gradient(to right, green, red); }\n\n/* Mouse-over effects */\n\n/* The slider handle (use -webkit- (Chrome, Opera, Safari, Edge) and -moz- (Firefox) to override default look) */\n\n.slider::-webkit-slider-thumb {\n  -webkit-appearance: none;\n  /* Override default look */\n  appearance: none;\n  width: 25px;\n  /* Set a specific slider handle width */\n  height: 25px;\n  /* Slider handle height */\n  background: white;\n  /* Green background */\n  cursor: pointer;\n  /* Cursor on hover */ }\n\n.slider::-moz-range-thumb {\n  width: 25px;\n  /* Set a specific slider handle width */\n  height: 25px;\n  /* Slider handle height */\n  background: white;\n  /* Green background */\n  cursor: pointer;\n  /* Cursor on hover */ }\n\ntextarea {\n  width: 100%;\n  color: black; }\n\n.calendar-img {\n  width: 34px;\n  height: 34px;\n  cursor: pointer; }\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/incidents/incident/incident.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export I18n */
/* unused harmony export CustomDatepickerI18n */
/* unused harmony export NgbDateNativeAdapter */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return IncidentComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__backend_backend_module__ = __webpack_require__("../../../../../src/app/backend/backend.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_common__ = __webpack_require__("../../../common/esm5/common.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__ng_bootstrap_ng_bootstrap__ = __webpack_require__("../../../../@ng-bootstrap/ng-bootstrap/index.js");
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};






var I18N_VALUES = {
    'zh-cn': {
        weekdays: ['一', '二', '三', '四', '五', '六', '日'],
        months: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'],
    }
    // other languages you would support
};
var I18n = /** @class */ (function () {
    function I18n() {
        this.language = 'zh-cn';
    }
    I18n = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["B" /* Injectable */])()
    ], I18n);
    return I18n;
}());

// Define custom service providing the months and weekdays translations
var CustomDatepickerI18n = /** @class */ (function (_super) {
    __extends(CustomDatepickerI18n, _super);
    function CustomDatepickerI18n(_i18n) {
        var _this = _super.call(this) || this;
        _this._i18n = _i18n;
        return _this;
    }
    CustomDatepickerI18n.prototype.getWeekdayShortName = function (weekday) {
        return I18N_VALUES[this._i18n.language].weekdays[weekday - 1];
    };
    CustomDatepickerI18n.prototype.getMonthShortName = function (month) {
        return I18N_VALUES[this._i18n.language].months[month - 1];
    };
    CustomDatepickerI18n.prototype.getMonthFullName = function (month) {
        return this.getMonthShortName(month);
    };
    CustomDatepickerI18n = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["B" /* Injectable */])(),
        __metadata("design:paramtypes", [I18n])
    ], CustomDatepickerI18n);
    return CustomDatepickerI18n;
}(__WEBPACK_IMPORTED_MODULE_4__ng_bootstrap_ng_bootstrap__["b" /* NgbDatepickerI18n */]));

/**
 * Example of a Native Date adapter
 */
var NgbDateNativeAdapter = /** @class */ (function (_super) {
    __extends(NgbDateNativeAdapter, _super);
    function NgbDateNativeAdapter() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    NgbDateNativeAdapter.prototype.fromModel = function (date) {
        return (date && date.getFullYear) ? { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() } : null;
    };
    NgbDateNativeAdapter.prototype.toModel = function (date) {
        return date ? new Date(date.year, date.month - 1, date.day) : null;
    };
    NgbDateNativeAdapter = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["B" /* Injectable */])()
    ], NgbDateNativeAdapter);
    return NgbDateNativeAdapter;
}(__WEBPACK_IMPORTED_MODULE_4__ng_bootstrap_ng_bootstrap__["a" /* NgbDateAdapter */]));

var IncidentComponent = /** @class */ (function () {
    function IncidentComponent(route, _location, incidentSrv) {
        this.route = route;
        this._location = _location;
        this.incidentSrv = incidentSrv;
        this.cats = [
            {
                value: 'BLACK',
                viewValue: '违规事件',
            },
            {
                value: 'RED',
                viewValue: '表彰事件',
            }
        ];
        this.incident = new __WEBPACK_IMPORTED_MODULE_1__backend_backend_module__["g" /* Incident */]();
        this.incident.category = 'BLACK';
        this.incident.severity = 5;
        this.incident.happenTime = new Date();
        this.incident.subject = new __WEBPACK_IMPORTED_MODULE_1__backend_backend_module__["m" /* Worker */]();
        var now = new Date();
        this.maxDate = {
            'year': now.getFullYear(),
            'month': now.getMonth() + 1,
            'day': now.getDate()
        };
    }
    IncidentComponent.prototype.ngOnInit = function () {
        var _this = this;
        var id = +this.route.snapshot.paramMap.get('id');
        if (Number.isNaN(id)) {
            this.isAdd = true;
            return;
        }
        this.isAdd = false;
        this.incidentSrv.getIncident(id).subscribe(function (incident) {
            _this.incident = incident;
        });
    };
    IncidentComponent.prototype.onSubmit = function () {
        var _this = this;
        if (this.isAdd) {
            this.incidentSrv.addIncident(this.incident).subscribe(function (incident) {
                _this._location.back();
            });
        }
        else {
            // update incident
            this.incidentSrv.updateIncident(this.incident).subscribe(function (incident) {
                _this._location.back();
            });
        }
    };
    IncidentComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            selector: 'app-incident',
            template: __webpack_require__("../../../../../src/app/incidents/incident/incident.component.html"),
            styles: [__webpack_require__("../../../../../src/app/incidents/incident/incident.component.scss")],
            providers: [{ provide: __WEBPACK_IMPORTED_MODULE_4__ng_bootstrap_ng_bootstrap__["a" /* NgbDateAdapter */], useClass: NgbDateNativeAdapter }, I18n,
                { provide: __WEBPACK_IMPORTED_MODULE_4__ng_bootstrap_ng_bootstrap__["b" /* NgbDatepickerI18n */], useClass: CustomDatepickerI18n }]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_2__angular_router__["a" /* ActivatedRoute */], __WEBPACK_IMPORTED_MODULE_3__angular_common__["f" /* Location */], __WEBPACK_IMPORTED_MODULE_1__backend_backend_module__["h" /* IncidentService */]])
    ], IncidentComponent);
    return IncidentComponent;
}());



/***/ }),

/***/ "../../../../../src/app/incidents/incidents.main.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return IncidentsMainComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var IncidentsMainComponent = /** @class */ (function () {
    function IncidentsMainComponent() {
    }
    IncidentsMainComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["n" /* Component */])({
            template: "\n    <router-outlet></router-outlet>\n  "
        })
    ], IncidentsMainComponent);
    return IncidentsMainComponent;
}());



/***/ }),

/***/ "../../../../../src/app/incidents/incidents.module.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IncidentsModule", function() { return IncidentsModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__incidents_main_component__ = __webpack_require__("../../../../../src/app/incidents/incidents.main.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_common__ = __webpack_require__("../../../common/esm5/common.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__incidents_incidents_component__ = __webpack_require__("../../../../../src/app/incidents/incidents/incidents.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__incident_incident_component__ = __webpack_require__("../../../../../src/app/incidents/incident/incident.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__angular_forms__ = __webpack_require__("../../../forms/esm5/forms.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7__ng_bootstrap_ng_bootstrap__ = __webpack_require__("../../../../@ng-bootstrap/ng-bootstrap/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__share_share_module__ = __webpack_require__("../../../../../src/app/share/share.module.ts");
/* harmony reexport (binding) */ __webpack_require__.d(__webpack_exports__, "IncidentsComponent", function() { return __WEBPACK_IMPORTED_MODULE_3__incidents_incidents_component__["a"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9__backend_policy_policy__ = __webpack_require__("../../../../../src/app/backend/policy/policy.ts");
/* harmony reexport (binding) */ __webpack_require__.d(__webpack_exports__, "Policy", function() { return __WEBPACK_IMPORTED_MODULE_9__backend_policy_policy__["b"]; });
/* harmony reexport (binding) */ __webpack_require__.d(__webpack_exports__, "Action", function() { return __WEBPACK_IMPORTED_MODULE_9__backend_policy_policy__["a"]; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10__backend_policy_condition__ = __webpack_require__("../../../../../src/app/backend/policy/condition.ts");
/* harmony reexport (binding) */ __webpack_require__.d(__webpack_exports__, "Condition", function() { return __WEBPACK_IMPORTED_MODULE_10__backend_policy_condition__["b"]; });
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};












var IncRoutes = [
    {
        path: '',
        component: __WEBPACK_IMPORTED_MODULE_0__incidents_main_component__["a" /* IncidentsMainComponent */],
        children: [
            {
                path: '',
                component: __WEBPACK_IMPORTED_MODULE_3__incidents_incidents_component__["a" /* IncidentsComponent */]
            },
            { path: ':id', component: __WEBPACK_IMPORTED_MODULE_5__incident_incident_component__["a" /* IncidentComponent */] }
        ]
    }
];
var IncidentsModule = /** @class */ (function () {
    function IncidentsModule() {
    }
    IncidentsModule = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_1__angular_core__["K" /* NgModule */])({
            imports: [
                __WEBPACK_IMPORTED_MODULE_7__ng_bootstrap_ng_bootstrap__["c" /* NgbModule */],
                __WEBPACK_IMPORTED_MODULE_2__angular_common__["b" /* CommonModule */],
                __WEBPACK_IMPORTED_MODULE_6__angular_forms__["a" /* FormsModule */],
                __WEBPACK_IMPORTED_MODULE_4__angular_router__["c" /* RouterModule */].forChild(IncRoutes),
                __WEBPACK_IMPORTED_MODULE_8__share_share_module__["b" /* ShareModule */]
            ],
            exports: [
                __WEBPACK_IMPORTED_MODULE_4__angular_router__["c" /* RouterModule */]
            ],
            declarations: [__WEBPACK_IMPORTED_MODULE_0__incidents_main_component__["a" /* IncidentsMainComponent */], __WEBPACK_IMPORTED_MODULE_3__incidents_incidents_component__["a" /* IncidentsComponent */], __WEBPACK_IMPORTED_MODULE_5__incident_incident_component__["a" /* IncidentComponent */]],
            providers: []
        })
    ], IncidentsModule);
    return IncidentsModule;
}());



/***/ }),

/***/ "../../../../../src/app/incidents/incidents/incidents.component.html":
/***/ (function(module, exports) {

module.exports = "<div class=\"input-group \">\n  <button class=\"btn btn-primary\" type=\"button\" routerLink=\"./new\" >添加</button>\n  <button class=\"btn btn-primary\" type=\"button\" (click)=\"performDelete()\">删除</button>\n  <input type=\"text\" class=\"form-control\" placeholder=\"查找...\">\n</div>\n<table class=\"table table-bordered table-hover\">\n  <thead>\n    <tr>\n      <th scope=\"col\"></th>\n      <th scope=\"col\">发生时间</th>\n      <th scope=\"col\">类型</th>\n      <th scope=\"col\">等级</th>\n      <th scope=\"col\">地点</th>\n      <th scope=\"col\">人员</th>\n      <th scope=\"col\">事件概要</th>\n    </tr>\n  </thead>\n  <tbody>\n    <tr *ngFor=\"let incident of incidents\">\n      <th scope=\"row\">\n        <input type=\"checkbox\" (change)=\"handleSelectEvent($event,incident)\"/>\n      </th>\n      <td><a href=\"#\" routerLink=\"./{{incident.id}}\">{{incident.happenTime | date:'yyyy-MM-dd':'+0800'}}</a></td>\n      <td>{{incident.category}}</td>\n      <td>{{incident.severity}}</td>\n      <td>{{incident.location}}</td>\n      <td>{{(incident.subject==null)?\"\":incident.subject.name}}</td>\n      <td>{{incident.title}}</td>\n    </tr>\n  </tbody>\n</table>\n<app-pagination [totalItems] = \"totalItems\" [pageSize]=\"pageable.size\" (onPageLoad)=\"loadPage($event)\"></app-pagination>"

/***/ }),

/***/ "../../../../../src/app/incidents/incidents/incidents.component.scss":
/***/ (function(module, exports, __webpack_require__) {

exports = module.exports = __webpack_require__("../../../../css-loader/lib/css-base.js")(false);
// imports


// module
exports.push([module.i, ".input-group {\n  position: relative;\n  width: 100%; }\n\n.input-group .form-control {\n  position: relative;\n  z-index: 2;\n  float: right;\n  width: 40%;\n  margin-bottom: 0; }\n\n.btn {\n  margin-right: 5px; }\n", ""]);

// exports


/*** EXPORTS FROM exports-loader ***/
module.exports = module.exports.toString();

/***/ }),

/***/ "../../../../../src/app/incidents/incidents/incidents.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return IncidentsComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_router__ = __webpack_require__("../../../router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__backend_backend_module__ = __webpack_require__("../../../../../src/app/backend/backend.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_core__ = __webpack_require__("../../../core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__messages_messages_module__ = __webpack_require__("../../../../../src/app/messages/messages.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__share_share_module__ = __webpack_require__("../../../../../src/app/share/share.module.ts");
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};





var IncidentsComponent = /** @class */ (function (_super) {
    __extends(IncidentsComponent, _super);
    function IncidentsComponent(router, incidentSrv, msgSrv) {
        var _this = _super.call(this) || this;
        _this.router = router;
        _this.incidentSrv = incidentSrv;
        _this.msgSrv = msgSrv;
        _this.selectedIncidents = new Set();
        return _this;
    }
    IncidentsComponent.prototype.ngOnInit = function () {
        this.reloadItems();
    };
    IncidentsComponent.prototype.reloadItems = function () {
        var _this = this;
        this.incidentSrv.getIncidentsx(this.pageable).subscribe(function (resp) {
            _this.totalItems = resp.totalElements;
            _this.incidents = resp.content;
        });
        this.selectedIncidents.clear();
    };
    IncidentsComponent.prototype.handleSelectEvent = function (e, incident) {
        if (e.target.checked) {
            this.selectedIncidents.add(incident);
        }
        else {
            this.selectedIncidents.delete(incident);
        }
    };
    IncidentsComponent.prototype.performDelete = function () {
        var _this = this;
        this.selectedIncidents.forEach(function (value, value2, set) {
            _this.incidentSrv.deleteIncident(value).subscribe(function (_) {
                console.log('incident deleted: ' + value.id);
                _this.msgSrv.addSuccess('事件删除成功：' + value.id);
                _this.reloadItems();
            });
        });
    };
    IncidentsComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_2__angular_core__["n" /* Component */])({
            selector: 'app-incidents',
            template: __webpack_require__("../../../../../src/app/incidents/incidents/incidents.component.html"),
            styles: [__webpack_require__("../../../../../src/app/incidents/incidents/incidents.component.scss")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_0__angular_router__["b" /* Router */], __WEBPACK_IMPORTED_MODULE_1__backend_backend_module__["h" /* IncidentService */], __WEBPACK_IMPORTED_MODULE_3__messages_messages_module__["a" /* MessageService */]])
    ], IncidentsComponent);
    return IncidentsComponent;
}(__WEBPACK_IMPORTED_MODULE_4__share_share_module__["a" /* PageableComponent */]));



/***/ })

});
//# sourceMappingURL=incidents.module.chunk.js.map