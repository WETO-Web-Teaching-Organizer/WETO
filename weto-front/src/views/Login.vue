<template>
    <v-card>
        <v-card-title>WETO Login</v-card-title>
        <v-card-text>
          <v-form v-model="isValid">
            <v-text-field label="Email" v-model="email" :rules="emailRules" required></v-text-field>
            <v-text-field label="Password" v-model="password" type="password" :rules="passwordRules" required></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn class="text--white primary" color="#4e008e" :disabled="!isValid">Login</v-btn>
        </v-card-actions>
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
    }
  }
</script>

<style>

</style>