<template>
  <v-app>
    <toolbar/>
    <v-content class="ml-4 mt-2">
      <router-view/>
    </v-content>
  </v-app>
</template>

<script>
  import Toolbar from './components/Toolbar.vue'
  import api from './backend-api'

  export default {
    name: "App",
    data: function (){
      return {
        errors: []
      }
    },
    components: {
      Toolbar
    },
    created(){
      this.checkLogin()
      this.getUser()
    },
    methods: {
      checkLogin(){
        api.pollLogin().catch(error => {
          this.errors.push(error)
          window.location.replace("http://localhost:8080/weto5/listCourses.action")
        })
      },
      getUser(){
        api.getUser().then(response => {
          this.$store.commit("logUser", JSON.parse(response.data))
        })
      }
    }
  }
</script>