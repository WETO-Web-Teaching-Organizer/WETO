<template>
  <div class="submissions">
    
    <div v-if="status === 'loading'" class="text-xs-center">
      <v-progress-circular indeterminate color="secondary" size="70" width="7"/>
    </div>
  
    <div v-if="status === 'error'" class="text-xs-center">
      <h4>An error has occurred</h4>
    </div>
    
    <div v-if="status === 'normal'">
      <h1 class="ma-8">{{ taskName }}</h1>
  
      <v-btn @click="getSubmissions">
        Submissions test
      </v-btn>
      
      <v-btn @click="getSubmission">
        Single submission test
      </v-btn>
    </div>
    
  </div>
</template>

<script>
  import api from '../backend-api'
  import router from '../router'

  export default {
    name: 'submission',
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
        return 4;
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
    created() {
      this.checkLogin();
      this.checkCourseSelection();
      this.fetchData();
    },
    watch: {
      taskId() {
        this.fetchData();
      }
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
      },
      getSubmissions() {
        api.getSubmissions(this.dbId, this.taskId, this.tabId).then(response => {
          console.log(response.data);
        })
      },
      getSubmission() {
        api.getSubmission(this.dbId, this.taskId, this.tabId).then(response => {
          console.log(response.data)
        })
      }
    }
  }
</script>