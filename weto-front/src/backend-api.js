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
  submitLogin(username, password) {
    return AXIOS.post('/submitLoginJSON', {username, password})
  },
  getCourseTask(db, task, tab) {
    return AXIOS.post('/viewJSONCourseTask', {dbId: db, taskId: task, tabId: tab})
  },
  // Submission APIs
  saveQuizAnswer(formData) {
    return AXIOS.post("/saveQuizAnswer", formData)
  },
  getSubmissions(db, task, tab) {
    return AXIOS.post("/getJSONSubmissions", {dbId: db, taskId: task, tabId: tab})
  },
  getDocuments(submissionId, dbId, taskId, tabId) {
    return AXIOS.get("/getJSONDocuments", { params: {submissionId, dbId, taskId, tabId} })
  },
  addSubmissionFile(fileName, submissionId, dbId, taskId, tabId) {
    return AXIOS.post("/addSubmissionFile", {documentFileFileName: fileName, submissionId, dbId, taskId, tabId})
  },
  getSubmission(db, task, tab) {
    return AXIOS.post("/getJSONSubmission", {dbId: db, taskId: task, tabId: tab})
  },
  // Grading APIs
  getJSONNodeGrades(dbId, taskId, tabId) {
    return AXIOS.post("/viewJSONNodeGrades", {dbId, taskId, tabId})
  },
  getJSONStudentLeafGrades(dbId, taskId, tabId) {
    return AXIOS.post("/viewJSONStudentLeafGrades", {dbId, taskId, tabId})
  },
  getJSONGrade(dbId, taskId, tabId) {
    return AXIOS.post("/viewJSONGrade", {dbId, taskId, tabId})
  }
}
