<template>
  <v-form>
    <v-simple-table dense>
      <tr>
        <th></th>
        <th v-for="choice in element.choices" :key="choice">
          {{ choice }}
        </th>
        <th v-if="elemDetail(element.detail)">
          {{ element.detail }}
        </th>
        <th></th>
      </tr>
      <tbody>
      <tr v-for="(question, index1) in element.questionTexts" :key="question">
        <td>{{ question }}</td>
        <td v-for="(choice, index2) in cleanChoices" :key="choice">
          <v-checkbox v-model="value[index1][index2]" :disabled="!quizOpen"/>
        </td>
        <td v-if="elemDetail(element.detail)">
          <v-text-field/>
        </td>
        <td>
          <v-btn class="secondary">Clear answer</v-btn>
        </td>
      </tr>
      </tbody>
    </v-simple-table>
    <v-btn class="primary" @click="submitAnswer()">Save answer</v-btn>
    <strong>
      Last saved:
      <span v-if="lastSave !== null">{{ lastSave }}</span>
      <span v-else>Never</span>
    </strong>
  </v-form>
</template>

<script>
  import api from '../backend-api'
  
  export default {
    name: "surveyQuiz",
    props: {
      element: Object,
      quizOpen: Boolean,
      taskId: Number,
      tabId: Number,
      dbId: Number
    },
    data: function () {
      return {
        qid: this.element.questionId,
        value: this.checkAnswers(this.element.userAnswers)
      }
    },
    computed: {
      cleanChoices() {
        let choices = []
        let i
        for (i = 0; i < this.element.choices.length; i++) {
          choices.push(this.element.choices[i].replace(/<\/?[^>]+(>|$)/g, ""))
        }
        return choices
      },
      lastSave: {
        set: function (newDate) {
          this.element.userAnswerDate = newDate
        },
        get: function () {
          return this.element.userAnswerDate
        }
      }
    },
    methods: {
      elemDetail(detail) {
        return detail !== null && detail !== '';
      },
      submitAnswer() {
        let i
        let answers = []
        for (i = 0; i < this.element.questionTexts.length; i++){
          let array = []
          answers.push(array)
          let n
          for (n = 0; n < this.element.choices.length; n++){
            if (this.value[i][n]){
              answers[i].push("true")
            } else {
              answers[i].push(null)
            }
          }
        }
        if(answers.length === 0){
          answers = null
        }
        let formData = new FormData()
        formData.append("taskId", this.taskId)
        formData.append("tabId", this.tabId)
        formData.append("dbId", this.dbId)
        formData.append("questionId", this.qid)
        formData.append("autograde", true)
        formData.append("testrun", false)
        if(answers !== null){
          formData.append("answers[" + this.qid + "][0]", JSON.stringify(this.value))
        }
        api.saveQuizAnswer(formData).then(response => {
          this.lastSave = response.data.split(';')[0]
        })
      },
      checkAnswers(userAnswers) {
        let answers = []
        let i
        for (i = 0; i < this.element.questionTexts.length; i++){
          let array = []
          answers.push(array)
          let n
          for (n = 0; n < this.element.choices.length; n++){
            if (userAnswers !== null && typeof userAnswers[i] !== 'undefined'){
              if (userAnswers[i][n] === null){
                answers[i].push(false)
              } else {
                answers[i].push(true)
              }
            } else {
              answers[i].push(false)
            }
          }
        }
        return answers
      }
    }
  }
</script>

<style scoped>

</style>