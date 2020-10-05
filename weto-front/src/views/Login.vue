<template>
    <v-card>
        <v-card-title>WETO Login</v-card-title>
        <v-card-text>
          <v-form v-model="isValid" ref="form">
            <v-text-field label="Email" v-model="email" :rules="emailRules" required></v-text-field>
            <v-text-field label="Password" v-model="password" type="password" :rules="passwordRules" required></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="submitLogin" class="text--white primary" color="#4e008e" :disabled="!isValid">Login</v-btn>
        </v-card-actions>
      <v-alert type="error" dismissible v-if="errors.length != 0">The username or password you entered was incorrect.</v-alert>
    </v-card>
</template>

<script>
  import api from '../backend-api'

  export default {
    name: 'login',
    data: () => ({
      email: null,
      emailRules: [
        v => !!v || 'E-mail is required',
        v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
      ],
      password: null,
      passwordRules: [
        v => !!v || 'Password is required'
      ],
      isValid: true,
      backendResponse: [],
      errors: [],
    }),
    methods: {
      checkLogin() {
        api.pollLogin().then(() => {
          window.location.replace("http://localhost:4545/");
        })
      },
      submitLogin() {
        const status = this.$store.getters.status;
        const taskId = this.$store.getters.selectedCourse.courseTaskId;
        const tabId = this.$store.getters.selectedCourse.tabId;
        const dbId = this.$store.getters.selectedCourse.databaseId;
        api.submitLogin(status, taskId, tabId, dbId, this.email, this.password).then(res => {
          this.backendResponse.push(res);
          //? add the response data into the store state?
        }).catch(err => {
          this.errors.push(err);
        })
      }
    }
  }
</script>

<style>

</style>