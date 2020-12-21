import Vue from 'vue'
import Router from 'vue-router'
import Task from './views/Task.vue'
import Forum from './views/Forum.vue'
import Groups from './views/Groups.vue'
import CourseList from './views/CourseList.vue'
import Login from './views/Login.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'login',
      component: Login
    },
    {
      path: '/courses',
      name: 'courseList',
      component: CourseList
    },
    {
      path: '/task',
      name: 'task',
      component: Task
    },
    {
      path: '/forum',
      name: 'forum',
      component: Forum
    },
    {
      path: '/groups',
      name: 'groups',
      component: Groups
    },
  ]
})
