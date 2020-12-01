<template>
  <div class="grading">
    <TaskGrade />
  </div>
</template>

<script>
  import api from '../backend-api'
  import TaskGrade from '../components/TaskGrade'

  export default {
    name: 'grading',
    computed: {
      taskId() {
        return this.$store.getters.currentTask;
      },
      dbId() {
        return this.$store.getters.selectedCourse.databaseId
      },
      tabId() {
        return this.$store.getters.selectedCourse.tabId;
      }
    },
    created() {
      this.checkLogin();
      this.checkCourseSelection();
    },
    components:{
      TaskGrade
    },
    methods: {
      checkLogin() {
        api.pollLogin().catch(() => {
          this.$router.replace("/")
        })
      },
      checkCourseSelection() {
        if (this.taskId === null || this.dbId === null) {
          this.clearSelectedCourse();
          this.$router.replace("/")
        }
      },
      clearSelectedCourse() {
        this.$store.commit("unselectCourse");
      }
    }
  }
</script>