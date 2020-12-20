import Vue from 'vue'
import Vuex from 'vuex'
import api from '../backend-api'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: {
    status: "normal",
    selectedCourse: {
      courseTaskId: null,
      databaseId: null,
      masterTaskId: null,
      tabId: null,
      name: ""
    },
    currentTask: null,
    subTasks: [],
    subTaskTree: [],
    user: {
      emailData: {value: ""},
      firstNameData: {value: ""},
      idData: {value: 0},
      idKC: {value: 0},
      lastNameData: {value: ""},
      loginNameData: {value: ""}
    }
  },

  plugins: [createPersistedState()],

  mutations: {
    changeStatus(state, newStatus) {
      state.status = newStatus;
    },
    selectCourse(state, course) {
      state.selectedCourse = {
        courseTaskId: course.courseTaskId,
        databaseId: course.databaseId,
        tabId: 0,
        masterTaskId: course.masterTaskId,
        name: course.name
      }
    },
    unselectCourse(state) {
      state.selectedCourse = {
        courseTaskId: null,
        subjectId: null,
        databaseId: null,
        masterTaskId: null,
        name: ""
      };
      state.currentTask = null;
      state.subTasks = [];
      state.subTaskTree = []
    },
    setSubTasks(state, subTasks) {
      state.subTasks = subTasks;
    },
    logUser(state, user) {
      state.user = user;
    },
    setTask(state, task) {
      state.currentTask = task;
    },
    createSubTaskTree(state, course) {
      state.subTaskTree = getChildSubTasks(course.databaseId, course.courseTaskId);
    }
  },

  getters: {
    status: state => state.status,
    selectedCourse: state => state.selectedCourse,
    subTasks: state => state.subTasks,
    currentTask: state => state.currentTask,
    currentTitle: state => state.currentTitle,
    user: state => state.user,
    subTaskTree: state => state.subTaskTree
  }
});

function getChildSubTasks(dbId, taskId) {
  let tasks = [];
  api.getCourseTask(dbId, taskId, 0).then(response => {
    if (response.data.subtasks.length > 0) {
      let i;
      for (i = 0; i < response.data.subtasks.length; i++) {
        tasks.push({
          name: response.data.subtasks[i].name,
          id: response.data.subtasks[i].id,
          children: getChildSubTasks(dbId, response.data.subtasks[i].id)
        });
      }
    }
  });
  return tasks;
}
