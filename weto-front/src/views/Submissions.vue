<template>
  <div class="submission">
    <file-submit v-bind:submission="submission" v-bind:file-patterns="filePatterns" v-on:add="createSubmission" v-on:refresh="getSubmissions"/>
    <user-submission
      v-if="submission !== null"
      v-bind:submission-status="submissionStatus"
      v-bind:submission="submission"
      v-bind:documents="documents"
      v-on:deleteDocument="getSubmissions"
    />
    <v-row v-if="submission" class="submissionActions" align="center" justify="space-around">
      <v-btn color="primary" @click="completeSubmission" :disabled="!documents.length || submissionStatus !== 'Not submitted'">
        <v-icon>check</v-icon>
        Complete submission
      </v-btn>
      <v-dialog
        v-model="dialog"
        width="400"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="error" v-bind="attrs" v-on="on">
            <v-icon>delete</v-icon>
            Delete submission
          </v-btn>
        </template>
        <v-card>
          <v-card-title>
            Delete submission
          </v-card-title>
          <v-card-text>
            Are you sure you want to delete this submission?
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="secondary" text @click="dialog = false">Cancel</v-btn>
            <v-btn color="primary" text @click="dialog = false; deleteSubmission(submission.id);">Delete</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
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
        filePatterns: "*.*",
        documents: [],
        dialog: false,
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
          window.location.replace("http://localhost:4545/");
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
          this.filePatterns = response.data.patternDescriptions;
          if (response.data.submissions.length) {
            const s = response.data.submissions[0];
            this.submission = s;
            this.submissionStatus = response.data.submissionStates[s.status];
            api.getDocuments(this.submission.id, this.dbId, this.taskId, this.tabId).then(res => {
              this.documents = res.data.documents;
            }).catch((err) => {
              console.log(err);
            });
          } else {
            this.submission = null;
            this.submissionStatus = null;
            this.documents = [];
          }
        }).catch(err => {
          console.log(err);
          this.errors.push(err);
        });
      },
      createSubmission() {
        api.createSubmission(this.user.idData.value, this.dbId, this.taskId, this.tabId).then(() => {
          this.getSubmissions();
        }).catch(err => {
          console.log(err);
        });
      },
      deleteSubmission() {
        api.deleteSubmission(this.submission.id, true, this.dbId, this.taskId, this.tabId).then(() => {
            this.getSubmissions();
        }).catch(err => {
            console.log(err);
          this.errors.push(err);
        });
      },
      completeSubmission() {
        api.completeSubmission(this.submission.id, this.dbId, this.taskId, this.tabId).then(() => {
          this.getSubmissions();
        }).catch(err => {
          console.log(err);
          this.errors.push(err);
        });
      }
    }
  }
</script>
<style scoped>
  .submissionActions {
    margin-top: 1em;
  }
  .submissionActions > * {
    margin-top: 1em;
  }
  .submission {
    padding: 1em;
    border: 1px #102027 solid;
    border-radius: 0.5em;
  }
</style>