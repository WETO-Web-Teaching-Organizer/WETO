<template>
  <div class="task">
    
    <div v-if="status === 'loading'" class="text-xs-center">
      <v-progress-circular indeterminate color="secondary" size="70" width="7"/>
    </div>
    
    <div v-if="status === 'error'" class="text-xs-center">
      <h4>Tapahtui virhe</h4>
    </div>
    
    <div v-if="status === 'normal'">
      <v-expansion-panels>
        <h1>{{ taskName }}</h1>
        
        <v-expansion-panel v-for="element in backendResponse.elements" :key="element.questionId" class="ma-5 mr-8">
          <div v-if="element.contentElementType === HTML" v-html="element.html" clasS="ma-3"/>
          <div v-else>
            <v-expansion-panel-header>
              <h2>
                {{ element.questionName }}
              </h2>
            </v-expansion-panel-header>
            <div v-if="element.contentElementType !== SURVEY">
              <div v-html="element.questionTexts[0]" class="ml-5"></div>
              
              <v-expansion-panel-content v-if="element.contentElementType === MULTIPLE_CHOICE">
                <single-choice-quiz v-if="element.singleAnswer" :element="element"
                                    :taskId="taskId" :tabId="tabId" :dbId="dbId"
                                    :quizOpen="backendResponse.quizOpen"/>
                <multi-choice-quiz v-else :element="element"
                                   :taskId="taskId" :tabId="tabId" :dbId="dbId"
                                   :quizOpen="backendResponse.quizOpen"/>
              </v-expansion-panel-content>
              
              <v-expansion-panel-content v-else-if="element.contentElementType === ESSAY">
                <essay-quiz :element="element" :taskId="taskId" :tabId="tabId" :dbId="dbId"
                            :quizOpen="backendResponse.quizOpen"/>
              </v-expansion-panel-content>
              
              <v-expansion-panel-content v-else-if="element.contentElementType === PROGRAM">
                <code-quiz :element="element" :taskId="taskId" :tabId="tabId" :dbId="dbId"
                           :quizOpen="backendResponse.quizOpen"/>
              </v-expansion-panel-content>
            
            </div>
            <v-expansion-panel-content v-else>
              <survey-quiz :element="element" :taskId="taskId" :tabId="tabId" :dbId="dbId"
                           :quizOpen="backendResponse.quizOpen"/>
            </v-expansion-panel-content>
          
          </div>
        </v-expansion-panel>
      </v-expansion-panels>
      
      <div v-if="typeof subTasks !== undefined">
        <div v-for="subTask in subTasks" :key="subTask.id">
          <v-btn rounded class="mb-2" @click="switchTask(subTask.id)">{{ subTask.name }}</v-btn>
        </div>
      </div>
    </div>
  
  </div>
</template>

<script>
  import api from '../backend-api'
  import router from '../router'
  import CodeQuiz from '../components/CodeQuiz'
  import EssayQuiz from '../components/EssayQuiz'
  import SingleChoiceQuiz from "../components/SingleChoiceQuiz";
  import MultiChoiceQuiz from '../components/MultiChoiceQuiz'
  import SurveyQuiz from '../components/SurveyQuiz'

  export default {
    name: 'task',
    data() {
      return {
        backendResponse: [],
        errors: [],

        HTML: 0,
        MULTIPLE_CHOICE: 1,
        ESSAY: 2,
        SURVEY: 3,
        PROGRAM: 4
      }
    },
    computed: {
      status() {
        return this.$store.getters.status;
      },
      taskId() {
        return this.$store.getters.currentTask;
      },
      dbId() {
        return this.$store.getters.selectedCourse.databaseId;
      },
      tabId() {
        return this.$store.getters.selectedCourse.tabId;
      },
      taskName() {
        return this.$store.getters.selectedCourse.name;
      },
      user() {
        return this.$store.getters.user;
      },
      subTasks() {
        return this.$store.getters.subTasks;
      }
    },
    mounted() {
      this.checkLogin();
      this.checkCourseSelection();
      this.fetchData();
    },
    watch: {
      taskId() {
        this.fetchData();
      }
    },
    components: {
      CodeQuiz,
      EssayQuiz,
      SingleChoiceQuiz,
      MultiChoiceQuiz,
      SurveyQuiz
    },
    methods: {
      checkLogin() {
        api.pollLogin().catch(error => {
          this.errors.push(error);
          window.location.replace("http://localhost:8080/weto5/listCourses.action");
        })
      },
      fetchData() {
        this.$store.commit("changeStatus", "loading");
        api.getCourseTask(this.dbId, this.taskId, this.tabId).then(response => {
          this.backendResponse = response.data;
          this.$store.commit("setSubTasks", response.data.subtasks);
          this.$store.commit("changeStatus", "normal");
        })
          .catch(error => {
            this.errors.push(error);
            this.$store.commit("changeStatus", "error");
          })
      },
      checkCourseSelection() {
        if (this.taskId === null || this.dbId === null) {
          this.clearSelectedCourse();
          router.push('/');
        }
      },
      clearSelectedCourse() {
        this.$store.commit("unselectCourse");
      },
      switchTask(id) {
        this.$store.commit("setTask", id);
      }
    }
  }
</script>

<style>

</style>