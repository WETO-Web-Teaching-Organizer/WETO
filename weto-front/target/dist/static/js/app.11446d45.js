(function(e){function t(t){for(var n,i,o=t[0],u=t[1],c=t[2],d=0,v=[];d<o.length;d++)i=o[d],a[i]&&v.push(a[i][0]),a[i]=0;for(n in u)Object.prototype.hasOwnProperty.call(u,n)&&(e[n]=u[n]);l&&l(t);while(v.length)v.shift()();return r.push.apply(r,c||[]),s()}function s(){for(var e,t=0;t<r.length;t++){for(var s=r[t],n=!0,o=1;o<s.length;o++){var u=s[o];0!==a[u]&&(n=!1)}n&&(r.splice(t--,1),e=i(i.s=s[0]))}return e}var n={},a={app:0},r=[];function i(t){if(n[t])return n[t].exports;var s=n[t]={i:t,l:!1,exports:{}};return e[t].call(s.exports,s,s.exports,i),s.l=!0,s.exports}i.m=e,i.c=n,i.d=function(e,t,s){i.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:s})},i.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},i.t=function(e,t){if(1&t&&(e=i(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var s=Object.create(null);if(i.r(s),Object.defineProperty(s,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var n in e)i.d(s,n,function(t){return e[t]}.bind(null,n));return s},i.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return i.d(t,"a",t),t},i.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},i.p="/";var o=window["webpackJsonp"]=window["webpackJsonp"]||[],u=o.push.bind(o);o.push=t,o=o.slice();for(var c=0;c<o.length;c++)t(o[c]);var l=u;r.push([0,"chunk-vendors"]),s()})({0:function(e,t,s){e.exports=s("56d7")},"56d7":function(e,t,s){"use strict";s.r(t);var n=s("2b0e"),a=s("ce5b"),r=s.n(a),i=(s("bf40"),{theme:{dark:!1,themes:{light:{primary:"#4e008e",secondary:"#3a4b54",accent:"#102027",info:"#c3b9d7",success:"#7dcdbe",warning:"#ffdca5",error:"#ff6e89",grey:"#c8c8c8",blue:"#82c8f0",pink:"#f5a5c8"}}},icons:{iconfont:"mdi"}});n["default"].use(r.a);var o=new r.a(i),u=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("v-app",[s("toolbar"),s("v-content",{staticClass:"ml-4 mt-2"},[s("router-view")],1)],1)},c=[],l=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("nav",[s("v-app-bar",{staticClass:"primary",attrs:{app:"",dark:"",dense:""}},[s("v-app-bar-nav-icon",{attrs:{dark:""},on:{click:function(t){e.sidebar=!e.sidebar}}}),s("v-divider",{attrs:{vertical:""}}),e.courseSelected()?s("v-toolbar-items",[s("v-btn",{staticClass:"hidden-sm-and-down",attrs:{text:"",router:"",to:"/task"}},[s("v-icon",{attrs:{left:""}},[e._v("home")]),s("span",[e._v("MAIN")])],1),s("v-btn",{staticClass:"hidden-sm-and-down",attrs:{text:"",router:"",to:"/grading"}},[s("v-icon",{attrs:{left:""}},[e._v("grade")]),s("span",[e._v("GRADING")])],1),s("v-btn",{staticClass:"hidden-sm-and-down",attrs:{text:"",router:"",to:"/submissions"}},[s("v-icon",{attrs:{left:""}},[e._v("subject")]),s("span",[e._v("SUBMISSIONS")])],1),s("v-btn",{staticClass:"hidden-sm-and-down",attrs:{text:"",router:"",to:"/forum"}},[s("v-icon",{attrs:{left:""}},[e._v("forum")]),s("span",[e._v("FORUM")])],1),s("v-btn",{staticClass:"hidden-sm-and-down",attrs:{text:"",router:"",to:"/groups"}},[s("v-icon",{attrs:{left:""}},[e._v("group")]),s("span",[e._v("GROUPS")])],1),s("v-btn",{staticClass:"hidden-md-and-up",attrs:{text:"",fab:"",router:"",to:"/task"}},[s("v-icon",[e._v("home")])],1),s("v-btn",{staticClass:"hidden-md-and-up",attrs:{text:"",fab:"",router:"",to:"/grading"}},[s("v-icon",[e._v("grade")])],1),s("v-btn",{staticClass:"hidden-md-and-up",attrs:{text:"",fab:"",router:"",to:"/submissions"}},[s("v-icon",[e._v("subject")])],1),s("v-btn",{staticClass:"hidden-md-and-up",attrs:{text:"",fab:"",router:"",to:"/forum"}},[s("v-icon",[e._v("forum")])],1),s("v-btn",{staticClass:"hidden-md-and-up",attrs:{text:"",fab:"",router:"",to:"/groups"}},[s("v-icon",[e._v("group")])],1)],1):e._e()],1),s("v-navigation-drawer",{staticClass:"info",attrs:{app:"",light:"",floating:""},model:{value:e.sidebar,callback:function(t){e.sidebar=t},expression:"sidebar"}},[s("v-app-bar",{staticClass:"primary",attrs:{dark:"",dense:""}},[s("v-btn",{staticClass:"font-weight-bold text-justify",attrs:{text:"",block:"",router:"",to:"/"}},[e._v("WETO")])],1),s("v-btn",{staticClass:"secondary",attrs:{block:"",text:""}},[s("v-icon",[e._v("face")]),s("span",[e._v("\n        "+e._s(this.$store.getters.user.firstNameData.value)+"\n        "+e._s(this.$store.getters.user.lastNameData.value)+"\n      ")])],1),s("v-divider"),""!==e.courseName?s("v-btn",{attrs:{block:"",text:""},on:{click:function(t){return e.selectRootTask()}}},[e._v(e._s(e.courseName))]):e._e(),s("v-treeview",{attrs:{rounded:"",hoverable:"",items:e.subTasks},on:{click:function(t){return e.testiFunktio()}},scopedSlots:e._u([{key:"label",fn:function(t){var n=t.item;return[s("a",{on:{click:function(t){return e.selectSubTask(n)}}},[e._v(e._s(n.name))])]}}])}),e.courseSelected()?s("v-list",{staticClass:"pa-0",attrs:{rounded:"",dense:""}},e._l(e.links,function(t){return s("v-list-item",{key:t.text,attrs:{router:"",to:t.route}},[s("v-list-item-content",[s("span",[e._v("\n            "+e._s(t.text)+"\n          ")])])],1)}),1):e._e()],1)],1)},d=[],v={name:"Toolbar.vue",data:function(){return{sidebar:!0,links:[{icon:"exit_to_app",text:"Return to course list",route:"/",toolbar:!1}]}},computed:{courseName:function(){return this.$store.getters.selectedCourse.name},subTasks:function(){return this.$store.getters.subTaskTree}},methods:{courseSelected:function(){return null!==this.$store.getters.selectedCourse.courseTaskId&&null!==this.$store.getters.selectedCourse.databaseId},selectRootTask:function(){this.$store.commit("setTask",this.$store.getters.selectedCourse.courseTaskId)},selectSubTask:function(e){this.$store.commit("setTask",e.id)}}},h=v,p=s("2877"),m=s("6544"),b=s.n(m),f=s("40dc"),k=s("5bc1"),g=s("8336"),_=s("ce7e"),I=s("132d"),C=s("8860"),w=s("da13"),T=s("5d23"),S=s("f774"),x=s("2a7f"),y=s("eb2a"),$=Object(p["a"])(h,l,d,!1,null,null,null),A=$.exports;b()($,{VAppBar:f["a"],VAppBarNavIcon:k["a"],VBtn:g["a"],VDivider:_["a"],VIcon:I["a"],VList:C["a"],VListItem:w["a"],VListItemContent:T["a"],VNavigationDrawer:S["a"],VToolbarItems:x["a"],VTreeview:y["a"]});var O=s("bc3a"),q=s.n(O),R=q.a.create({baseURL:"weto5/",timeout:1e3}),V={pollLogin:function(){return R.get("/pollLogin")},getCourses:function(){return R.get("/getJSONCourses")},getUser:function(){return R.get("/getUser")},getRealUser:function(){return R.get("/getRealUser")},getCourseTask:function(e,t,s){return R.post("/viewJSONCourseTask",{dbId:e,taskId:t,tabId:s})},saveQuizAnswer:function(e){return R.post("/saveQuizAnswer",e)},getSubmissions:function(e,t,s){return R.get("/getJSONSubmissions",{dbId:e,taskId:t,tabId:s})}},E={name:"App",data:function(){return{errors:[]}},components:{Toolbar:A},mounted:function(){this.checkLogin(),this.getUser()},methods:{checkLogin:function(){var e=this;V.pollLogin().catch(function(t){e.errors.push(t),window.location.replace("http://localhost:8080/weto5/listCourses.action")})},getUser:function(){var e=this;V.getUser().then(function(t){console.log(JSON.parse(t.data)),e.$store.commit("logUser",JSON.parse(t.data))})}}},z=E,N=s("7496"),D=s("a75b"),L=Object(p["a"])(z,u,c,!1,null,null,null),P=L.exports;b()(L,{VApp:N["a"],VContent:D["a"]});var j=s("2f62");n["default"].use(j["a"]);var M=new j["a"].Store({state:{status:"normal",selectedCourse:{courseTaskId:null,databaseId:null,masterTaskId:null,tabId:null,name:""},currentTask:null,subTasks:[],subTaskTree:[],user:{emailData:{value:""},firstNameData:{value:""},idData:{value:0},idKC:{value:0},lastNameData:{value:""},loginNameData:{value:""}}},mutations:{changeStatus:function(e,t){e.status=t},selectCourse:function(e,t){e.selectedCourse={courseTaskId:t.courseTaskId,databaseId:t.databaseId,tabId:0,masterTaskId:t.masterTaskId,name:t.name}},unselectCourse:function(e){e.selectedCourse={courseTaskId:null,subjectId:null,databaseId:null,masterTaskId:null,name:""},e.currentTask=null,e.subTasks=[],e.subTaskTree=[]},setSubTasks:function(e,t){e.subTasks=t},logUser:function(e,t){e.user=t},setTask:function(e,t){e.currentTask=t},createSubTaskTree:function(e,t){e.subTaskTree=[],e.subTaskTree=U(t.databaseId,t.courseTaskId)}},getters:{status:function(e){return e.status},selectedCourse:function(e){return e.selectedCourse},subTasks:function(e){return e.subTasks},currentTask:function(e){return e.currentTask},currentTitle:function(e){return e.currentTitle},user:function(e){return e.user},subTaskTree:function(e){return e.subTaskTree}}});function U(e,t){var s=[];return V.getCourseTask(e,t,0).then(function(t){var n;if(t.data.subtasks.length>0)for(n=0;n<t.data.subtasks.length;n++)s.push({name:t.data.subtasks[n].name,id:t.data.subtasks[n].id,children:U(e,t.data.subtasks[n].id)})}),s}var Q=s("8c4f"),F=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"task"},["loading"===e.status?s("div",{staticClass:"text-xs-center"},[s("v-progress-circular",{attrs:{indeterminate:"",color:"secondary",size:"70",width:"7"}})],1):e._e(),"error"===e.status?s("div",{staticClass:"text-xs-center"},[s("h4",[e._v("Tapahtui virhe")])]):e._e(),"normal"===e.status?s("div",[s("v-expansion-panels",[s("h1",[e._v(e._s(e.taskName))]),e._l(e.backendResponse.elements,function(t){return s("v-expansion-panel",{key:t.questionId,staticClass:"ma-5 mr-8"},[t.contentElementType===e.HTML?s("div",{attrs:{clasS:"ma-3"},domProps:{innerHTML:e._s(t.html)}}):s("div",[null!==t.userAnswers?s("div"):e._e(),s("div",[s("v-expansion-panel-header",[s("h2",[e._v("\n                "+e._s(t.questionName)+"\n              ")])]),t.contentElementType!==e.SURVEY?s("div",[s("div",{staticClass:"ml-5",domProps:{innerHTML:e._s(t.questionTexts[0])}}),t.contentElementType===e.MULTIPLE_CHOICE?s("v-expansion-panel-content",[t.singleAnswer?s("single-choice-quiz",{attrs:{element:t,taskId:e.taskId,tabId:e.tabId,dbId:e.dbId,quizOpen:e.backendResponse.quizOpen}}):s("multi-choice-quiz",{attrs:{element:t,taskId:e.taskId,tabId:e.tabId,dbId:e.dbId,quizOpen:e.backendResponse.quizOpen}})],1):t.contentElementType===e.ESSAY?s("v-expansion-panel-content",[s("essay-quiz",{attrs:{element:t,taskId:e.taskId,tabId:e.tabId,dbId:e.dbId,quizOpen:e.backendResponse.quizOpen}})],1):t.contentElementType===e.PROGRAM?s("v-expansion-panel-content",[s("code-quiz",{attrs:{element:t,taskId:e.taskId,tabId:e.tabId,dbId:e.dbId,quizOpen:e.backendResponse.quizOpen}})],1):e._e()],1):s("v-expansion-panel-content",[s("survey-quiz",{attrs:{element:t,taskId:e.taskId,tabId:e.tabId,dbId:e.dbId,quizOpen:e.backendResponse.quizOpen}})],1)],1)])])})],2),void 0!==typeof e.subTasks?s("div",e._l(e.subTasks,function(t){return s("div",{key:t.id},[s("v-btn",{staticClass:"mb-2",attrs:{rounded:""},on:{click:function(s){return e.switchTask(t.id)}}},[e._v(e._s(t.name))])],1)}),0):e._e()],1):e._e()])},B=[],H=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("v-form",[e.quizOpen?s("div",[s("fieldset",[s("editor",{attrs:{lang:"html",theme:"chrome",height:"150"},on:{init:e.editorInit},model:{value:e.value,callback:function(t){e.value=t},expression:"value"}})],1),s("v-btn",{staticClass:"primary",on:{click:function(t){return e.submitCode()}}},[e._v("Save answer")]),s("strong",[e._v("\n      Last saved:\n      "),null!==e.lastSave?s("span",[e._v("\n        "+e._s(e.lastSave)+"\n      ")]):s("span",[e._v("Never")])]),s("div",{staticClass:"testField my-5"},[s("v-text-field",{staticClass:"my-0",attrs:{label:"Test #","hide-details":"",outlined:""}}),s("v-btn",{staticClass:"secondary my-0",on:{click:function(t){return e.saveAndTestCode()}}},[e._v("Test run")])],1),s("v-btn",{staticClass:"primary",on:{click:function(t){return e.saveAndSubmitCode()}}},[e._v("Save and submit")])],1):s("div",[s("fieldset",[s("editor",{attrs:{value:e.value,lang:"html",theme:"chrome",height:"150",disabled:"true"},on:{init:e.editorInit}})],1),s("v-btn",{staticClass:"primary",attrs:{disabled:""}},[e._v("Save answer")]),s("strong",[e._v("\n      Last saved:\n      "),null!==e.lastSave?s("span",[e._v("\n        "+e._s(e.lastSave)+"\n      ")]):s("span",[e._v("Never")])]),s("div",{staticClass:"testField my-5"},[s("v-text-field",{staticClass:"my-0",attrs:{label:"Test #","hide-details":"",outlined:""}}),s("v-btn",{staticClass:"secondary my-0",attrs:{disabled:""}},[e._v("Test run")])],1),s("v-btn",{staticClass:"primary",attrs:{disabled:""}},[e._v("Save and submit")])],1)])},J=[],G={name:"codeQuiz",props:{element:Object,quizOpen:Boolean,taskId:Number,tabId:Number,dbId:Number},data:function(){return{value:this.checkAnswer(this.element.userAnswers)}},computed:{qid:function(){return this.element.questionId},lastSave:{set:function(e){this.element.userAnswerDate=e},get:function(){return this.element.userAnswerDate}}},components:{editor:s("7c9e")},methods:{editorInit:function(){s("2099"),s("be9d"),s("bb36"),s("0329"),s("95b8"),s("6a21")},submitCode:function(){var e=this,t=new FormData;t.append("taskId",this.taskId),t.append("tabId",this.tabId),t.append("dbId",this.dbId),t.append("questionId",this.qid),t.append("autograde",!0),t.append("testrun",!1),null!==this.value&&t.append("answers["+this.qid+"][0]",this.value),V.saveQuizAnswer(t).then(function(t){e.lastSave=t.data.split(";")[0]})},saveAndTestCode:function(){var e=this,t=new FormData;t.append("taskId",this.taskId),t.append("tabId",this.tabId),t.append("dbId",this.dbId),t.append("questionId",this.qid),t.append("autograde",!0),t.append("testrun",!0),null!==this.value&&t.append("answers["+this.qid+"][0]",this.value),V.saveQuizAnswer(t).then(function(t){e.lastSave=t.data.split(";")[0]})},saveAndSubmitCode:function(){var e=this,t=new FormData;t.append("taskId",this.taskId),t.append("tabId",this.tabId),t.append("dbId",this.dbId),t.append("questionId",this.qid),t.append("autograde",!0),t.append("testrun",!0),null!==this.value&&t.append("answers["+this.qid+"][0]",this.value),V.saveQuizAnswer(t).then(function(t){e.lastSave=t.data.split(";")[0]})},checkAnswer:function(e){return null===e?"":this.element.userAnswers[0][0]}}},Y=G,K=s("4bd4"),W=s("8654"),X=Object(p["a"])(Y,H,J,!1,null,"00ceb451",null),Z=X.exports;b()(X,{VBtn:g["a"],VForm:K["a"],VTextField:W["a"]});var ee=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("v-form",[e.quizOpen?s("div",[s("editor",{attrs:{init:{plugins:"wordcount"}},model:{value:e.value,callback:function(t){e.value=t},expression:"value"}}),s("v-btn",{staticClass:"primary",on:{click:function(t){return e.submitEssay()}}},[e._v("Save answer")]),s("strong",[e._v("\n      Last saved:\n      "),null!==e.element.userAnswerDate?s("span",[e._v("\n        "+e._s(e.element.userAnswerDate)+"\n      ")]):s("span",[e._v("Never")])])],1):s("div",[s("editor",{attrs:{init:{plugins:"wordcount"},disabled:""},model:{value:e.value,callback:function(t){e.value=t},expression:"value"}}),s("v-btn",{attrs:{disabled:""}},[e._v("Save answer")]),s("strong",[e._v("\n      Last saved:\n      "),null!==e.lastSave?s("span",[e._v("\n        "+e._s(e.lastSave)+"\n      ")]):s("span",[e._v("Never")])])],1)])},te=[],se=s("ca72"),ne={name:"essayQuiz",components:{editor:se["a"]},props:{element:Object,quizOpen:Boolean,taskId:Number,tabId:Number,dbId:Number},data:function(){return{value:this.checkAnswer(this.element.userAnswers),qid:this.element.questionId}},computed:{lastSave:{set:function(e){this.element.userAnswerDate=e},get:function(){return this.element.userAnswerDate}}},methods:{submitEssay:function(){var e=this,t=new FormData;t.append("taskId",this.taskId),t.append("tabId",this.tabId),t.append("dbId",this.dbId),t.append("questionId",this.qid),t.append("autograde",!1),t.append("testrun",!1),null!==this.value&&t.append("answers["+this.qid+"][0]",this.value),V.saveQuizAnswer(t).then(function(t){e.lastSave=t.data.split(";")[0]})},checkAnswer:function(e){if(null===e)return"";var t=document.createElement("textarea");return t.innerHTML=this.element.userAnswers[0][0],t.value.replace(/<\/?[^>]+(>|$)/g,"").trim()}}},ae=ne,re=Object(p["a"])(ae,ee,te,!1,null,"2ff6c1b9",null),ie=re.exports;b()(re,{VBtn:g["a"],VForm:K["a"]});var oe=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("v-form",[s("v-radio-group",{model:{value:e.value,callback:function(t){e.value=t},expression:"value"}},e._l(e.cleanChoices,function(t,n){return s("v-radio",{key:t,attrs:{label:e.labelMaker(n,t),value:n,disabled:!e.quizOpen}})}),1),e.quizOpen?s("div",[s("v-btn",{staticClass:"secondary",on:{click:function(t){return e.clearAll()}}},[e._v("Clear answer")]),s("v-btn",{staticClass:"primary",on:{click:function(t){return e.submitAnswerEvent(e.element.questionId)}}},[e._v("Save answer")]),s("strong",[e._v("\n      Last saved:\n      "),null!==e.lastSave?s("span",[e._v("\n        "+e._s(e.lastSave)+"\n      ")]):s("span",[e._v("Never")])])],1):e._e()],1)},ue=[],ce={name:"multiChoiceQuiz",props:{element:Object,quizOpen:Boolean,taskId:Number,tabId:Number,dbId:Number},data:function(){return{qid:this.element.questionId,value:this.arrayToInt(this.checkAnswers(this.element.userAnswers))}},computed:{cleanChoices:function(){var e,t=[];for(e=0;e<this.element.choices.length;e++)t.push(this.element.choices[e].replace(/<\/?[^>]+(>|$)/g,""));return t},lastSave:{set:function(e){this.element.userAnswerDate=e},get:function(){return this.element.userAnswerDate}}},methods:{submitAnswerEvent:function(){var e,t=this,s=null;for(e=0;e<this.element.choices.length;e++)this.value===e&&(s=this.element.choices[e]);var n=new FormData;n.append("taskId",this.taskId),n.append("tabId",this.tabId),n.append("dbId",this.dbId),n.append("questionId",this.qid),n.append("autograde",!0),n.append("testrun",!1),null!==s&&n.append("answers["+this.qid+"][0]",s),V.saveQuizAnswer(n).then(function(e){t.lastSave=e.data.split(";")[0]})},labelMaker:function(e,t){return String(e+1+" "+t)},clearAll:function(){this.value=this.arrayToInt([])},arrayToInt:function(e){var t;for(t=0;t<e.length;t++)if(e[t])return t;return-1},checkAnswers:function(e){return null===e?[]:e[0]}}},le=ce,de=s("67b6"),ve=s("43a6"),he=Object(p["a"])(le,oe,ue,!1,null,"adcc4980",null),pe=he.exports;b()(he,{VBtn:g["a"],VForm:K["a"],VRadio:de["a"],VRadioGroup:ve["a"]});var me=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("v-form",[e._l(e.cleanChoices,function(t,n){return s("v-checkbox",{key:t,attrs:{label:e.labelMaker(n,t),disabled:!e.quizOpen},model:{value:e.value[n],callback:function(t){e.$set(e.value,n,t)},expression:"value[index]"}})}),e.quizOpen?s("div",[s("v-btn",{staticClass:"secondary",on:{click:function(t){return e.clearAll()}}},[e._v("Clear answer")]),s("v-btn",{staticClass:"primary",on:{click:e.submitAnswer}},[e._v("Save answer")]),s("strong",[e._v("\n      Last saved:\n      "),null!==e.lastSave?s("span",[e._v("\n        "+e._s(e.lastSave)+"\n      ")]):s("span",[e._v("Never")])])],1):e._e()],2)},be=[],fe={name:"multiChoiceQuiz",props:{element:Object,quizOpen:Boolean,taskId:Number,tabId:Number,dbId:Number},data:function(){return{qid:this.element.questionId,value:this.checkAnswers(this.element.userAnswers)}},computed:{cleanChoices:function(){var e,t=[];for(e=0;e<this.element.choices.length;e++)t.push(this.element.choices[e].replace(/<\/?[^>]+(>|$)/g,""));return t},lastSave:{set:function(e){this.element.userAnswerDate=e},get:function(){return this.element.userAnswerDate}}},methods:{submitAnswer:function(){var e,t=this,s=[];for(e=0;e<this.element.choices.length;e++)this.value[e]&&s.push(this.element.choices[e]);0===s.length&&(s=null);var n=new FormData;n.append("taskId",this.taskId),n.append("tabId",this.tabId),n.append("dbId",this.dbId),n.append("questionId",this.qid),n.append("autograde",!0),n.append("testrun",!1),null!==s&&n.append("answers["+this.qid+"][0]",s),V.saveQuizAnswer(n).then(function(e){t.lastSave=e.data.split(";")[0]})},labelMaker:function(e,t){return String(e+1+" "+t)},clearAll:function(){var e;for(this.value=[],e=0;e<this.element.choices.length;e++)this.value.push(null)},checkAnswers:function(e){return null===e?new Array(this.element.choices.length):e[0]}}},ke=fe,ge=s("ac7c"),_e=Object(p["a"])(ke,me,be,!1,null,"a8a5f984",null),Ie=_e.exports;b()(_e,{VBtn:g["a"],VCheckbox:ge["a"],VForm:K["a"]});var Ce=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("v-form",[s("v-simple-table",{attrs:{dense:""}},[s("tr",[s("th"),e._l(e.element.choices,function(t){return s("th",{key:t},[e._v("\n        "+e._s(t)+"\n      ")])}),e.elemDetail(e.element.detail)?s("th",[e._v("\n        "+e._s(e.element.detail)+"\n      ")]):e._e(),s("th")],2),s("tbody",e._l(e.element.questionTexts,function(t,n){return s("tr",{key:t},[s("td",[e._v(e._s(t))]),e._l(e.cleanChoices,function(t,a){return s("td",{key:t},[s("v-checkbox",{attrs:{disabled:!e.quizOpen},model:{value:e.value[n][a],callback:function(t){e.$set(e.value[n],a,t)},expression:"value[index1][index2]"}})],1)}),e.elemDetail(e.element.detail)?s("td",[s("v-text-field")],1):e._e(),s("td",[s("v-btn",{staticClass:"secondary"},[e._v("Clear answer")])],1)],2)}),0)]),s("v-btn",{staticClass:"primary",on:{click:function(t){return e.submitAnswer()}}},[e._v("Save answer")]),s("strong",[e._v("\n    Last saved:\n    "),null!==e.lastSave?s("span",[e._v(e._s(e.lastSave))]):s("span",[e._v("Never")])])],1)},we=[],Te={name:"surveyQuiz",props:{element:Object,quizOpen:Boolean,taskId:Number,tabId:Number,dbId:Number},data:function(){return{qid:this.element.questionId,value:this.checkAnswers(this.element.userAnswers)}},computed:{cleanChoices:function(){console.log("element"),console.log(this.element);var e,t=[];for(e=0;e<this.element.choices.length;e++)t.push(this.element.choices[e].replace(/<\/?[^>]+(>|$)/g,""));return t},lastSave:{set:function(e){this.element.userAnswerDate=e},get:function(){return this.element.userAnswerDate}}},methods:{elemDetail:function(e){return null!==e&&""!==e},submitAnswer:function(){var e,t=this,s=[];for(e=0;e<this.element.questionTexts.length;e++){var n=[];s.push(n);var a=void 0;for(a=0;a<this.element.choices.length;a++)this.value[e][a]?s[e].push("true"):s[e].push(null)}console.log("answers on nyt tällainen ennen submittiä:"),console.log(s),0===s.length&&(s=null);var r=new FormData;r.append("taskId",this.taskId),r.append("tabId",this.tabId),r.append("dbId",this.dbId),r.append("questionId",this.qid),r.append("autograde",!0),r.append("testrun",!1),null!==s&&r.append("answers["+this.qid+"][0]",JSON.stringify(this.value)),console.log(r),V.saveQuizAnswer(r).then(function(e){t.lastSave=e.data.split(";")[0]})},checkAnswers:function(e){console.log("userAnswers:"),console.log(e);var t,s=[];for(t=0;t<this.element.questionTexts.length;t++){var n=[];s.push(n);var a=void 0;for(a=0;a<this.element.choices.length;a++)null!==e&&"undefined"!==typeof e[t]?null===e[t][a]?s[t].push(!1):s[t].push(!0):s[t].push(!1)}return s}}},Se=Te,xe=s("1f4f"),ye=Object(p["a"])(Se,Ce,we,!1,null,"0df36b10",null),$e=ye.exports;b()(ye,{VBtn:g["a"],VCheckbox:ge["a"],VForm:K["a"],VSimpleTable:xe["a"],VTextField:W["a"]});var Ae={name:"task",data:function(){return{backendResponse:[],errors:[],HTML:0,MULTIPLE_CHOICE:1,ESSAY:2,SURVEY:3,PROGRAM:4}},computed:{status:function(){return this.$store.getters.status},taskId:function(){return this.$store.getters.currentTask},dbId:function(){return this.$store.getters.selectedCourse.databaseId},tabId:function(){return this.$store.getters.selectedCourse.tabId},taskName:function(){return this.$store.getters.selectedCourse.name},user:function(){return this.$store.getters.user},subTasks:function(){return this.$store.getters.subTasks}},mounted:function(){this.checkLogin(),this.checkCourseSelection(),this.fetchData()},watch:{$route:"fetchData",taskId:function(){this.fetchData()}},components:{CodeQuiz:Z,EssayQuiz:ie,SingleChoiceQuiz:pe,MultiChoiceQuiz:Ie,SurveyQuiz:$e},methods:{checkLogin:function(){var e=this;V.pollLogin().catch(function(t){e.errors.push(t),window.location.replace("http://localhost:8080/weto5/listCourses.action")})},fetchData:function(){var e=this;this.$store.commit("changeStatus","loading"),V.getCourseTask(this.dbId,this.taskId,this.tabId).then(function(t){e.backendResponse=t.data,e.$store.commit("setSubTasks",t.data.subtasks),e.$store.commit("changeStatus","normal")}).catch(function(t){e.errors.push(t),e.$store.commit("changeStatus","error")})},checkCourseSelection:function(){null!==this.taskId&&null!==this.dbId||(this.clearSelectedCourse(),Ct.push("/"))},clearSelectedCourse:function(){this.$store.commit("unselectCourse")},switchTask:function(e){this.$store.commit("setTask",e)}}},Oe=Ae,qe=s("cd55"),Re=s("49e2"),Ve=s("c865"),Ee=s("0393"),ze=s("490a"),Ne=Object(p["a"])(Oe,F,B,!1,null,null,null),De=Ne.exports;b()(Ne,{VBtn:g["a"],VExpansionPanel:qe["a"],VExpansionPanelContent:Re["a"],VExpansionPanelHeader:Ve["a"],VExpansionPanels:Ee["a"],VProgressCircular:ze["a"]});var Le=function(){var e=this,t=e.$createElement;e._self._c;return e._m(0)},Pe=[function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"about"},[s("h1",[e._v("This is an about page")])])}],je={},Me=je,Ue=Object(p["a"])(Me,Le,Pe,!1,null,null,null),Qe=Ue.exports,Fe=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"submissions"},[s("h1",[e._v("This is a submissions page")]),s("v-btn",{on:{click:function(t){return e.getSubmissions()}}},[e._v("Print stuff")])],1)},Be=[],He={name:"submission",data:function(){return{backendResponse:[],errors:[],HTML:0,MULTIPLE_CHOICE:1,ESSAY:2,SURVEY:3,PROGRAM:4}},computed:{status:function(){return this.$store.getters.status},taskId:function(){return this.$store.getters.currentTask},dbId:function(){return this.$store.getters.selectedCourse.databaseId},tabId:function(){return 4},taskName:function(){return this.$store.getters.selectedCourse.name},user:function(){return this.$store.getters.user},subTasks:function(){return this.$store.getters.subTasks}},mounted:function(){this.checkLogin(),this.checkCourseSelection(),this.fetchData()},watch:{$route:"fetchData",taskId:function(){this.fetchData()}},methods:{checkLogin:function(){var e=this;V.pollLogin().catch(function(t){e.errors.push(t),window.location.replace("http://localhost:8080/weto5/listCourses.action")})},fetchData:function(){var e=this;this.$store.commit("changeStatus","loading"),V.getCourseTask(this.dbId,this.taskId,this.tabId).then(function(t){e.backendResponse=t.data,e.$store.commit("setSubTasks",t.data.subtasks),e.$store.commit("changeStatus","normal")}).catch(function(t){e.errors.push(t),e.$store.commit("changeStatus","error")})},checkCourseSelection:function(){null!==this.taskId&&null!==this.dbId||(this.clearSelectedCourse(),Ct.push("/"))},clearSelectedCourse:function(){this.$store.commit("unselectCourse")},switchTask:function(e){this.$store.commit("setTask",e)},printStuff:function(){console.log(this.backendResponse)},getSubmissions:function(){V.getSubmissions(this.dbId,this.taskId,this.tabId).then(function(e){console.log(e.data)})}}},Je=He,Ge=Object(p["a"])(Je,Fe,Be,!1,null,null,null),Ye=Ge.exports;b()(Ge,{VBtn:g["a"]});var Ke=function(){var e=this,t=e.$createElement;e._self._c;return e._m(0)},We=[function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"grading"},[s("h1",[e._v("This is a grading page")])])}],Xe={},Ze=Xe,et=Object(p["a"])(Ze,Ke,We,!1,null,null,null),tt=et.exports,st=function(){var e=this,t=e.$createElement;e._self._c;return e._m(0)},nt=[function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"forum"},[s("h1",[e._v("This is a forum page")])])}],at={},rt=at,it=Object(p["a"])(rt,st,nt,!1,null,null,null),ot=it.exports,ut=function(){var e=this,t=e.$createElement;e._self._c;return e._m(0)},ct=[function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"groups"},[s("h1",[e._v("This is a groups page")])])}],lt={},dt=lt,vt=Object(p["a"])(dt,ut,ct,!1,null,null,null),ht=vt.exports,pt=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"courseList"},["loading"===e.status?s("div",{staticClass:"text-xs-center"},[s("v-progress-circular",{attrs:{indeterminate:"",color:"secondary",size:"70",width:"7"}})],1):e._e(),"error"===e.status?s("div",{staticClass:"text-xs-center"},[s("h4",[e._v("Tapahtui virhe")])]):e._e(),"normal"===e.status?s("div",e._l(e.backendResponse.subjects,function(t){return s("v-container",{key:t.name},[s("v-card",[s("h1",{staticClass:"subjects pl-2"},[e._v(e._s(t.name))])]),s("v-card",[s("div",{attrs:{clasS:"activeCourses"}},[null!==e.backendResponse.registeredCourses[t.id]?s("v-list",[s("h3",{staticClass:"pl-2"},[e._v("Your active courses")]),e._l(e.backendResponse.registeredCourses[t.id],function(t){return s("v-list-item",{key:t.name},[s("v-list-item-content",[s("v-btn",{staticClass:"primary",attrs:{rounded:""},on:{click:function(s){return e.selectCourse(t)}}},[e._v("\n                  "+e._s(t.name)+"\n                  "),null!==e.backendResponse.viewPeriods[t.masterTaskId][0]||null!==e.backendResponse.viewPeriods[t.masterTaskId][1]?s("span",[e._v("\n                  ("+e._s(e.backendResponse.viewPeriods[t.masterTaskId][0])+" -\n                  "+e._s(e.backendResponse.viewPeriods[t.masterTaskId][1])+")\n                ")]):e._e()])],1)],1)})],2):e._e()],1)]),s("v-card",[s("div",{staticClass:"availableCourses"},[null!==e.backendResponse.courses[t.id]?s("v-list",{staticClass:"availableCourses"},[s("v-list-item",[s("v-list-item-content",[s("h3",[e._v("Available courses")])])],1),e._l(e.backendResponse.courses[t.id],function(t){return s("v-list-item",{key:t.name},[s("v-list-item-content",[s("v-btn",{staticClass:"secondary",attrs:{rounded:""},on:{click:function(s){return e.selectCourse(t)}}},[e._v("\n                  "+e._s(t.name)+"\n                  (Registration open until:\n                  "+e._s(e.backendResponse.registerPeriods[t.masterTaskId][1])+" )\n                ")]),s("h4",[e._v(e._s(t.name)+"\n                  (Registration open until:\n                  "+e._s(e.backendResponse.registerPeriods[t.masterTaskId][1])+" )\n                ")])],1)],1)})],2):e._e()],1)]),s("v-card",[null!==e.backendResponse.inactiveCourses?s("div",{staticClass:"inactiveCourses"},[s("v-expansion-panels",[s("v-expansion-panel",[s("v-expansion-panel-header",{staticClass:"my-2"},[s("h3",[e._v("Inactive courses")])]),s("v-expansion-panel-content",[s("v-list",e._l(e.backendResponse.inactiveCourses,function(t){return s("v-list-item",{key:t.name},[s("v-list-item-content",[s("v-btn",{attrs:{rounded:""},on:{click:function(s){return e.selectCourse(t)}}},[e._v("\n                        "+e._s(t.name)+"\n                        "),null!==e.backendResponse.viewPeriods[t.masterTaskId][0]||null!==e.backendResponse.viewPeriods[t.masterTaskId][1]?s("span",[e._v("\n                       ("+e._s(e.backendResponse.viewPeriods[t.masterTaskId][0])+" -\n                        "+e._s(e.backendResponse.viewPeriods[t.masterTaskId][1])+")\n                    ")]):e._e()])],1)],1)}),1)],1)],1)],1)],1):e._e()])],1)}),1):e._e()])},mt=[],bt={name:"course-list",data:function(){return{backendResponse:[],errors:[],user:this.$store.getters.user}},computed:{status:function(){return this.$store.getters.status}},mounted:function(){this.checkLogin(),this.getUser(),this.clearSelectedCourse(),this.fetchData()},watch:{$route:"fetchData"},methods:{checkLogin:function(){var e=this;V.pollLogin().catch(function(t){e.errors.push(t),window.location.replace("http://localhost:8080/weto5/listCourses.action")})},fetchData:function(){var e=this;this.$store.commit("changeStatus","loading"),V.getCourses().then(function(t){e.backendResponse=t.data,e.$store.commit("changeStatus","normal")}).catch(function(t){e.errors.push(t),e.$store.commit("changeStatus","error")})},selectCourse:function(e){this.$store.commit("selectCourse",e),this.$store.commit("setTask",e.courseTaskId),this.$store.commit("createSubTaskTree",e),Ct.push("/task")},clearSelectedCourse:function(){this.$store.commit("unselectCourse")},getUser:function(){var e=this;V.getUser().then(function(t){e.$store.commit("logUser",JSON.parse(t.data))})}}},ft=bt,kt=(s("5ab8"),s("b0af")),gt=s("a523"),_t=Object(p["a"])(ft,pt,mt,!1,null,null,null),It=_t.exports;b()(_t,{VBtn:g["a"],VCard:kt["a"],VContainer:gt["a"],VExpansionPanel:qe["a"],VExpansionPanelContent:Re["a"],VExpansionPanelHeader:Ve["a"],VExpansionPanels:Ee["a"],VList:C["a"],VListItem:w["a"],VListItemContent:T["a"],VProgressCircular:ze["a"]}),n["default"].use(Q["a"]);var Ct=new Q["a"]({mode:"history",base:"/",routes:[{path:"/",name:"courseList",component:It},{path:"/task",name:"task",component:De},{path:"/about",name:"about",component:Qe},{path:"/grading",name:"grading",component:tt},{path:"/submissions",name:"submissions",component:Ye},{path:"/forum",name:"forum",component:ot},{path:"/groups",name:"groups",component:ht}]});n["default"].config.productionTip=!1,new n["default"]({store:M,router:Ct,vuetify:o,render:function(e){return e(P)}}).$mount("#app")},"5ab8":function(e,t,s){"use strict";var n=s("6575"),a=s.n(n);a.a},6575:function(e,t,s){}});
//# sourceMappingURL=app.11446d45.js.map