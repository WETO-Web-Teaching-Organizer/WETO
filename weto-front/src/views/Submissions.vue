<template>
  <div class="submissions">
    <h1>This is a submissions page</h1>
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
      }
    }
  }
</script>