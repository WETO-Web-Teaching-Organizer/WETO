<template>
  <v-form>
    <v-radio-group v-model="value">
      <v-radio v-for="(choice, index) in cleanChoices" :key="choice" :label="labelMaker(index, choice)"
               :value="index" :disabled="!quizOpen"/>
    </v-radio-group>
    <div v-if="quizOpen">
      <v-btn class="secondary" @click="clearAll()">Clear answer</v-btn>
      <v-btn class="primary" @click="submitAnswerEvent(element.questionId)">Save answer</v-btn>
      <strong>
        Last saved:
        <span v-if="lastSave !== null">
          {{ lastSave }}
        </span>
        <span v-else>Never</span>
      </strong>
    </div>
  </v-form>
</template>

<script>
  import api from '../backend-api'
  
  export default {
    name: "multiChoiceQuiz" +
      "",
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
        value: this.arrayToInt(this.checkAnswers(this.element.userAnswers))
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
        set: function(newDate){
          this.element.userAnswerDate = newDate
        },
        get: function(){
          return this.element.userAnswerDate
        }
      }
    },
    methods: {
      submitAnswerEvent() {
        let i
        let answer = null
        for(i = 0; i < this.element.choices.length; i++) {
          if(this.value === i) {
            answer = this.element.choices[i]
          }
        }
        let formData = new FormData()
        formData.append("taskId", this.taskId)
        formData.append("tabId", this.tabId)
        formData.append("dbId", this.dbId)
        formData.append("questionId", this.qid)
        formData.append("autograde", true)
        formData.append("testrun", false)
        if(answer !== null){
          formData.append("answers[" + this.qid + "][0]", answer)
        }
        api.saveQuizAnswer(formData).then(response => {
          this.lastSave = response.data.split(';')[0]
        })
      },
      labelMaker(index, choice) {
        return String((index + 1) + " " + choice)
      },
      clearAll() {
        this.value = this.arrayToInt([])
      },
      arrayToInt(array) {
        let i
        for(i = 0; i < array.length; i++) {
          if(array[i]) {
            return i
          }
        }
        return -1
      },
      checkAnswers(userAnswers) {
        if(userAnswers === null) {
          return []
        } else {
          return userAnswers[0]
        }
      }
    }
  }
</script>

<style scoped>

</style>