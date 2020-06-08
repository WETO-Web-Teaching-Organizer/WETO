<template>
  <v-form>
    
    <div v-if="quizOpen">
      <fieldset>
        <editor v-model="value" @init="editorInit" lang="html" theme="chrome" height="150"/>
      </fieldset>
      <v-btn class="primary" @click="submitCode()">Save answer</v-btn>
      <strong>
        Last saved:
        <span v-if="lastSave !== null">
          {{ lastSave }}
        </span>
        <span v-else>Never</span>
      </strong>
      <div class="testField my-5">
        <v-text-field label="Test #" hide-details outlined class="my-0"/>
        <v-btn class="secondary my-0" @click="saveAndTestCode()">Test run</v-btn>
      </div>
      <v-btn class="primary" @click="saveAndSubmitCode()">Save and submit</v-btn>
    </div>
    
    <div v-else>
      <fieldset>
        <editor :value="value" @init="editorInit" lang="html" theme="chrome" height="150" disabled="true"/>
      </fieldset>
      <v-btn class="primary" disabled>Save answer</v-btn>
      <strong>
        Last saved:
        <span v-if="lastSave !== null">
          {{ lastSave }}
        </span>
        <span v-else>Never</span>
      </strong>
      <div class="testField my-5">
        <v-text-field label="Test #" hide-details outlined class="my-0"/>
        <v-btn class="secondary my-0" disabled>Test run</v-btn>
      </div>
      <v-btn class="primary" disabled>Save and submit</v-btn>
    </div>
    
  </v-form>
</template>

<script>
  import api from '../backend-api'

  export default {
    name: "codeQuiz",
    props: {
      element: Object,
      quizOpen: Boolean,
      taskId: Number,
      tabId: Number,
      dbId: Number
    },
    data(){
      return {
        value: this.checkAnswer(this.element.userAnswers)
      }
    },
    computed: {
      qid(){
        return this.element.questionId
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
    components: {
      editor: require('vue2-ace-editor'),
    },
    methods: {
      editorInit(){
        require('brace/ext/language_tools') //language extension prerequsite...
        require('brace/mode/html')
        require('brace/mode/javascript')    //language
        require('brace/mode/less')
        require('brace/theme/chrome')
        require('brace/snippets/javascript') //snippet
      },
      submitCode(){
        let formData = new FormData()
        formData.append("taskId", this.taskId)
        formData.append("tabId", this.tabId)
        formData.append("dbId", this.dbId)
        formData.append("questionId", this.qid)
        formData.append("autograde", true)
        formData.append("testrun", false)
        if(this.value !== null){
          formData.append("answers[" + this.qid + "][0]", this.value)
        }
        api.saveQuizAnswer(formData).then(response => {
          this.lastSave = response.data.split(';')[0]
        })
      },
      saveAndTestCode(){
        let formData = new FormData()
        formData.append("taskId", this.taskId)
        formData.append("tabId", this.tabId)
        formData.append("dbId", this.dbId)
        formData.append("questionId", this.qid)
        formData.append("autograde", true)
        formData.append("testrun", true)
        if(this.value !== null){
          formData.append("answers[" + this.qid + "][0]", this.value)
        }
        api.saveQuizAnswer(formData).then(response => {
          this.lastSave = response.data.split(';')[0]
        })
      },
      saveAndSubmitCode(){
        let formData = new FormData()
        formData.append("taskId", this.taskId)
        formData.append("tabId", this.tabId)
        formData.append("dbId", this.dbId)
        formData.append("questionId", this.qid)
        formData.append("autograde", true)
        formData.append("testrun", true)
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
          return this.element.userAnswers[0][0]
        }
      }
    }
  }
</script>

<style scoped>

</style>