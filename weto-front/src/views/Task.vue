<template>
  <div class="task">

    <div v-if="status === 'loading'" class="text-xs-center">
      <v-progress-circular indeterminate color="secondary" size="70" width="7"/>
    </div>

    <div v-if="status === 'error'" class="text-xs-center">
      <h4>An error has occurred</h4>
    </div>

    <div v-if="status === 'normal'">
      <h1 class="titles">{{ courseName }}</h1>
      <h2 v-if="taskName !== courseName" class="titles">{{ taskName }}</h2>

      <div v-if="elements.length > 0 && elements[0].contentElementType === HTML" id="html">
        <div v-html="backendResponse.elements[0].html" id="task"/>
        <div id="tabs">
          <submission v-if="tabs.includes('Submissions')" id="submission"/>
          <grading v-if="tabs.includes('Grading')" id="grading"/>
        </div>
      </div>
      <v-expansion-panels v-else>
        <v-expansion-panel v-for="element in elements" :key="element.questionId" class="ma-5 mr-8">
          <div>
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
          <v-btn rounded class="mb-2" @click="switchTask(subTask)">{{ subTask.name }}</v-btn>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
  import api from '../backend-api'
  import CodeQuiz from '../components/CodeQuiz'
  import EssayQuiz from '../components/EssayQuiz'
  import SingleChoiceQuiz from "../components/SingleChoiceQuiz";
  import MultiChoiceQuiz from '../components/MultiChoiceQuiz'
  import SurveyQuiz from '../components/SurveyQuiz'
  import Submission from './Submissions'
  import Grading from './Grading'

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
        PROGRAM: 4,
        tabs: []
      }
    },
    computed: {
      status() {
        return this.$store.getters.status;
      },
      taskId() {
        return this.$store.getters.currentTask.id || this.$store.getters.currentTask.courseTaskId;
      },
      dbId() {
        return this.$store.getters.selectedCourse.databaseId;
      },
      tabId() {
        return this.$store.getters.selectedCourse.tabId;
      },
      courseName() {
        return this.$store.getters.selectedCourse.name;
      },
      taskName() {
        return this.$store.getters.currentTask.name;
      },
      user() {
        return this.$store.getters.user;
      },
      subTasks() {
        return this.$store.getters.subTasks;
      },
      elements() {
        return this.backendResponse.elements;
      }
    },
    created() {
      this.checkLogin();
      this.checkCourseSelection();
      this.fetchTabs();
      this.fetchData();
    },
    watch: {
      taskId() {
        this.fetchTabs();
        this.fetchData();
      }
    },
    components: {
      CodeQuiz,
      EssayQuiz,
      SingleChoiceQuiz,
      MultiChoiceQuiz,
      SurveyQuiz,
      Submission,
      Grading
    },
    methods: {
      checkLogin() {
        api.pollLogin().catch(error => {
          this.errors.push(error);
          this.$router.replace('/');
        })
      },
      fetchTabs() {
        api.getTask(this.taskId, this.tabId, this.dbId).then(res => {
          const myregexp = /<span[^>]+?class="tabmenu-link".*?>([\s\S]*?)<\/span>/g;
          let match = myregexp.exec(res.data);
          let result = [];
          while(match !== null) {
            result.push(RegExp.$1.trim());
            match = myregexp.exec(res.data);
          }
          this.tabs = result;
        }).catch(err => {
          this.errors.push(err);
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
          this.$router.push('/');
        }
      },
      clearSelectedCourse() {
        this.$store.commit("unselectCourse");
      },
      switchTask(task) {
        this.$store.commit("setTask", task);
      }
    }
  }
</script>

<style>
  .titles {
    margin: 1.5rem;
  }
  #tabs {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  #tabs > * {
    margin-bottom: 1.5em;
  }
  #task {
    margin: 3em 1em;
  }

  @media screen and (min-width: 1366px){
    #submission { max-width: 80vw;}
    #grading { max-width: 80vw;}
  }
</style>