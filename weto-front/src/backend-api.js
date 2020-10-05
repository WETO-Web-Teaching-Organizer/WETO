import axios from 'axios'

const AXIOS = axios.create({
  baseURL: 'weto5/',
  timeout: 1000
});


export default {
  pollLogin() {
    return AXIOS.get('/pollLogin')
  },
  getCourses() {
    return AXIOS.get('/getJSONCourses')
  },
  getUser() {
    return AXIOS.get('/getUser')
  },
  getRealUser() {
    return AXIOS.get('/getRealUser')
  },
  submitLogin(status, taskId, tabId, dbId, username, password) {
    return AXIOS.post('/submitLogin', {status, taskId, tabId, dbId, username, password})
  },
  getCourseTask(db, task, tab) {
    return AXIOS.post('/viewJSONCourseTask', {dbId: db, taskId: task, tabId: tab})
  },
  saveQuizAnswer(formData) {
    return AXIOS.post("/saveQuizAnswer", formData)
  },
  getSubmissions(db, task, tab) {
    return AXIOS.post("/getJSONSubmissions", {dbId: db, taskId: task, tabId: tab})
  },
  getSubmission(db, task, tab) {
    return AXIOS.post("/getJSONSubmission", {dbId: db, taskId: task, tabId: tab})
  }
}
