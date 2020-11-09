<template>
  <v-simple-table>
    <template v-slot:default>
      <thead>
        <tr>
          <th class="text-left">Name</th>
          <th class="text-left">Date modified</th>
          <th class="text-left">Status</th>
          <th class="text-left">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="doc in documents" :key="doc.id"
        >
          <td>{{ doc.fileName }}</td>
          <td>{{ doc.fileDate }}</td>
          <td>{{ submissionStatus }}</td>
          <td>
            <v-row align="center" justify="flex-start">
              <v-btn depressed color="primary" class="docAction" @click="downloadSubmissionFile(doc.fileName, doc.id)">
                <v-icon>download</v-icon>
                Download
              </v-btn>
              <!-- <v-btn depressed color="secondary" class="docAction">Edit</v-btn> -->
              <v-dialog
                v-model="dialog"
                width="400"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-btn depressed color="error" class="docAction" v-bind="attrs" v-on="on">
                    <v-icon>delete</v-icon>
                    Delete
                  </v-btn>
                </template>
                <v-card>
                  <v-card-title>
                    Delete file from submission
                  </v-card-title>
                  <v-card-text>
                    Are you sure you want to delete the file: {{doc.fileName}}?
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="secondary" text @click="dialog = false">Cancel</v-btn>
                    <v-btn color="primary" text @click="dialog = false; deleteSubmissionFile(doc.id)">Delete</v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </v-row>
          </td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
</template>

<script>
  import api from '../backend-api'
  export default {
    name: 'userSubmission',
    data() {
      return {
        backendResponse: [],
        errors: [],
        documents: null,
        dialog: false,
        HTML: 0,
        MULTIPLE_CHOICE: 1,
        ESSAY: 2,
        SURVEY: 3,
        PROGRAM: 4,
        submissionStatus: null
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
      this.fetchData();
      this.getDocuments();
    },
    watch: {
      taskId() {
        this.fetchData();
      }
    },
    methods: {
      fetchData() {
        this.$store.commit("changeStatus", "loading");
        api.getCourseTask(this.dbId, this.taskId, this.tabId).then(response => {
          this.backendResponse = response.data;
          this.$store.commit("setSubTasks", response.data.subtasks);
          this.$store.commit("changeStatus", "normal");
        }).catch(error => {
          this.errors.push(error);
          this.$store.commit("changeStatus", "error");
        })
      },
      getDocuments() {
        let submissionId;
        api.getSubmissions(this.dbId, this.taskId, this.tabId).then(response => {
          const submission = response.data.submissions[0];
          submissionId = submission.id;
          this.submissionStatus = response.data.submissionStates[submission.status];
        }).then(() => {
          api.getDocuments(submissionId, this.dbId, this.taskId, this.tabId).then(res => {
            this.documents = res.data.documents;
          }).catch((err) => {
            console.log(err);
          })
        }).catch(err => {
          console.log(err);
          this.errors.push(err);
        })
      },
      downloadSubmissionFile(filename, id) {
        api.downloadSubmissionFile(filename, id, this.dbId, this.taskId, this.tabId);
      },
      deleteSubmissionFile(id) {
        api.deleteSubmissionFile(id, this.dbId, this.taskId, this.tabId).then(() => {
          this.getDocuments();
        }).catch(err => {
          console.log(err);
          this.backendResponse.push(err);
        });
      },
    }
  }
</script>

<style scoped>
  thead {
    background-color: #f3e5ff;
  }
  .docAction {
    margin: 0 2%;
  }
</style>