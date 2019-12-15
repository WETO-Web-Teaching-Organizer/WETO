<template>
  <v-form>
    <div v-if="quizOpen">
      <editor :init="{plugins: 'wordcount'}" v-model="value" />
      <v-btn class="primary" @click="submitEssay()">Save answer</v-btn>
      <strong>
        Last saved:
        <span v-if="element.userAnswerDate !== null">
          {{ element.userAnswerDate }}
        </span>
        <span v-else>Never</span>
      </strong>
    </div>
    <div v-else>
      <editor :init="{plugins: 'wordcount'}" v-model="value" disabled />
      <v-btn disabled>Save answer</v-btn>
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
  import Editor from '@tinymce/tinymce-vue'


  export default {
    name: "essayQuiz",
    components: {
      'editor': Editor
    },
    props: {
      element: Object,
      quizOpen: Boolean,
      taskId: Number,
      tabId: Number,
      dbId: Number
    },
    data: function(){
      return {
        value: this.checkAnswer(this.element.userAnswers),
        qid: this.element.questionId
      }
    },
    computed: {
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
      submitEssay(){
        let formData = new FormData()
        formData.append("taskId", this.taskId)
        formData.append("tabId", this.tabId)
        formData.append("dbId", this.dbId)
        formData.append("questionId", this.qid)
        formData.append("autograde", false)
        formData.append("testrun", false)
        if(this.value !== null){
          formData.append("answers[" + this.qid + "][0]", this.value)
        }
        api.saveQuizAnswer(formData).then(response => {
          this.lastSave = response.data.split(';')[0]
        })
      },
      checkAnswer(userAnswers){
        if (userAnswers === null){
          return ""
        } else {
          let txt = document.createElement("textarea")
          txt.innerHTML = this.element.userAnswers[0][0]
          return txt.value.replace(/<\/?[^>]+(>|$)/g, "").trim()
        }
      }
    }
  }
</script>

<style scoped>

</style>