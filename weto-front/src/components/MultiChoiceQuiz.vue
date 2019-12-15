<template>
  <v-form>
    <v-checkbox v-for="(choice, index) in cleanChoices" :key="choice" :label="labelMaker(index, choice)"
                v-model="value[index]" :disabled="!quizOpen"/>
    <div v-if="quizOpen">
      <v-btn class="secondary" @click="clearAll()">Clear answer</v-btn>
      <v-btn class="primary" @click="submitAnswer">Save answer</v-btn>
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
    name: "multiChoiceQuiz",
    props: {
      element: Object,
      quizOpen: Boolean,
      taskId: Number,
      tabId: Number,
      dbId: Number
    },
    data: function (){
      return {
        qid: this.element.questionId,
        value: this.checkAnswers(this.element.userAnswers)
      }
    },
    computed: {
      cleanChoices(){
        let choices = []
        let i
        for (i = 0; i < this.element.choices.length; i++){
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
      submitAnswer(){
        let i
        let answers = []
        for (i = 0; i < this.element.choices.length; i++){
          if (this.value[i]){
            answers.push(this.element.choices[i])
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
          formData.append("answers[" + this.qid + "][0]", answers)
        }
        api.saveQuizAnswer(formData).then(response => {
          this.lastSave = response.data.split(';')[0]
        })
      },
      labelMaker(index, choice){
        return String((index + 1) + " " + choice)
      },
      clearAll(){
        this.value = []
        let i
        for (i = 0; i < this.element.choices.length; i++) {
          this.value.push(null)
        }
      },
      checkAnswers(userAnswers){
        if (userAnswers === null){
          return new Array(this.element.choices.length)
        } else {
          return userAnswers[0]
        }
      }
    }
  }
</script>

<style scoped>

</style>