<template>
  <div class="submission">
    <FileSubmit/>
    <UserSubmission v-if="submissionStatus === 'Accepted' || submissionStatus === 'Not submitted'"/>
    <v-row class="submissionActions" align="center" justify="space-between">
      <v-btn color="primary" @click="completeSubmission">
        <v-icon>check</v-icon>
        Complete submission
      </v-btn>
      <v-btn color="error" @click="deleteSubmission">
        <v-icon>delete</v-icon>
        Delete submission
      </v-btn>
    </v-row>
  </div>
</template>

<script>
  import api from '../backend-api'
  import router from '../router'
  import UserSubmission from '../components/UserSubmission'
  import FileSubmit from '../components/FileSubmit.vue'

  export default {
    name: 'submission',
    data() {
      return {
        backendResponse: [],
        errors: [],
        submission: null,
        submissionStatus: null,
        HTML: 0,
        MULTIPLE_CHOICE: 1,
        ESSAY: 2,
        SURVEY: 3,
        PROGRAM: 4
      }
    },
    computed: {
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
      this.getSubmissions();
    },
    watch: {
      taskId() {
        this.fetchData();
      }
    },
    components: {
      UserSubmission,
      FileSubmit
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
          this.submission = response.data.submissions[0];
          this.submissionStatus = response.data.submissionStates[2];
        });
      },
      deleteSubmission() {
        const submitted = this.submissionStatus === 'Accepted'? true : false;
        api.deleteSubmission(this.submission.id, submitted, this.dbId, this.taskId, this.tabId).catch(err => {
          console.log(err);
          this.errors.push(err);
        })
      },
      completeSubmission() {
        api.completeSubmission(this.submissionId, this.dbId, this.taskId, this.tabId).catch(err => {
          console.log(err);
          this.errors.push(err);
        })
      }
    }
  }
</script>
<style scoped>
  .submissionActions {
    margin: 1em 0em;
  }
</style>